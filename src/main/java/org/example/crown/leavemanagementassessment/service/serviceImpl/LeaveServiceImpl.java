package org.example.crown.leavemanagementassessment.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.crown.leavemanagementassessment.dto.LeaveDecisionDto;
import org.example.crown.leavemanagementassessment.dto.LeaveRequest;
import org.example.crown.leavemanagementassessment.dto.LeaveResponse;
import org.example.crown.leavemanagementassessment.entity.Employee;
import org.example.crown.leavemanagementassessment.entity.LeaveRecords;
import org.example.crown.leavemanagementassessment.enums.Decision;
import org.example.crown.leavemanagementassessment.enums.LeaveStatus;
import org.example.crown.leavemanagementassessment.exception.*;
import org.example.crown.leavemanagementassessment.repository.EmployeeRepository;
import org.example.crown.leavemanagementassessment.repository.LeaveRepository;
import org.example.crown.leavemanagementassessment.service.LeaveService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;

    private static final int ANNUAL_LEAVE_LIMIT = 20;
    private static final List<LeaveStatus> RESERVED_STATUSES = List.of(LeaveStatus.PENDING_STATUS, LeaveStatus.APPROVE_STATUS);

    /**
     *
     * @param leaveRequest
     * @return
     */
    @Override
    public LeaveResponse submitRequest(LeaveRequest leaveRequest) throws InvalidDateRequestException {

        // retrieve the employee
        Employee employee = employeeRepository.findById(leaveRequest.getEmployeeId())
                .orElseThrow(() -> new UserNotFoundException("Employee not found"));

        // validate dates
        validateDates(leaveRequest.getStartDate(), leaveRequest.getEndDate());

        // calculate working days
        int leaveDays = calculateLeaveDays(leaveRequest.getStartDate(), leaveRequest.getEndDate());

        if (leaveDays == 0) {
            throw new RuntimeException("Leave request must contain at least one working day");
        }

        // check overrlapping request
        boolean overlapping = leaveRepository.existsByIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employee.getId(),
                RESERVED_STATUSES,
                leaveRequest.getEndDate(),
                leaveRequest.getStartDate()
        );

        if (overlapping) {
            throw new OverlappingException("You already have an overlapping leave request");
        }

        // 5. Calculate available leave balance
        Long reservedDays = leaveRepository.sumReservedDays(
                employee.getId(),
                RESERVED_STATUSES
        );

        int availableBalance = ANNUAL_LEAVE_LIMIT - reservedDays.intValue();

        if (leaveDays > availableBalance) {
            throw new InsufficientLeaveException(
                    "Insufficient leave balance. Available days: "
                            + availableBalance
            );
        }

        // create leave request
        LeaveRecords leaveRecord = new LeaveRecords();
        leaveRecord.setEmployeeId(employee.getId());
        leaveRecord.setStartDate(leaveRequest.getStartDate());
        leaveRecord.setEndDate(leaveRequest.getEndDate());
        leaveRecord.setLeaveDays(leaveDays);
        leaveRecord.setReason(leaveRequest.getReason());
        leaveRecord.setDataInitiated(LocalDateTime.now());
        leaveRecord.setStatus(LeaveStatus.PENDING_STATUS);

        LeaveRecords savedRequest = leaveRepository.save(leaveRecord);

        return mapToResponse(savedRequest);
    }

    @Override
    public LeaveResponse retrieveRequestById(Long id) {
        LeaveRecords foundRecord = leaveRepository.findLeaveRecordsById(id).orElseThrow(() ->
                new RecordNotFoundException("Leave record does not exist"));
        return mapToResponse(foundRecord);
    }

    /**
     *
     * @param leaveRequestId
     * @param leaveDecisionDto
     * @return
     */
    @Override
    @Transactional
    public LeaveResponse decideLeaveRequest(Long leaveRequestId, LeaveDecisionDto leaveDecisionDto) {

        // 1. Retrieve the leave request
        LeaveRecords leaveRequest = leaveRepository
                .findByIdForUpdate(leaveRequestId)
                .orElseThrow(() ->
                        new RecordNotFoundException(
                                "Leave request not found: " + leaveRequestId
                        )
                );

        // 2. Only pending requests can be approved or rejected
        if (leaveRequest.getStatus() != LeaveStatus.PENDING_STATUS) {
            throw new InvalidRequestException(
                    "Only pending leave requests can be approved or rejected"
            );
        }

        // 3. Retrieve and lock the employee
        Employee employee = employeeRepository
                .findByIdForUpdate(leaveRequest.getEmployeeId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found")
                );

        // 4. Process manager's decision
        if (leaveDecisionDto.getDecision() == Decision.APPROVE) {

            // Calculate all reserved leave days
            Long reservedDays = leaveRepository.sumReservedDays(
                    employee.getId(),
                    RESERVED_STATUSES
            );

            // Ensure the employee's entitlement is not exceeded
            if (reservedDays > ANNUAL_LEAVE_LIMIT) {
                throw new RuntimeException(
                        "Employee's annual leave balance has been exceeded"
                );
            }

            // Approve request
            leaveRequest.setStatus(LeaveStatus.APPROVE_STATUS);

        } else if (leaveDecisionDto.getDecision() == Decision.REJECT) {

            // Reject request
            leaveRequest.setStatus(LeaveStatus.REJECT_STATUS);

        }

        // 5. Record decision details
        leaveRequest.setManagerId(leaveDecisionDto.getManagerId());
        leaveRequest.setDecisionComment(leaveDecisionDto.getComment());
        leaveRequest.setDecidedAt(LocalDateTime.now());

        // 6. Save updated request
        LeaveRecords updatedRequest =
                leaveRepository.save(leaveRequest);

        // 7. Return response
        return mapToResponses(updatedRequest);
    }

    /**
     *
     * @param employeeId
     * @param page
     * @param size
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LeaveResponse> getLeaveRequests(Long employeeId, int page, int size) {

        // Validate pagination parameters
        if (page < 0) {
            throw new RuntimeException("Page number cannot be negative");
        }

        if (size <= 0 || size > 100) {
            throw new RuntimeException("Page size must be between 1 and 100");
        }

        // Configure pagination and sorting
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "dataInitiated")
        );

        Page<LeaveRecords> leaveRecords;

        // Filter by employee if employeeId is provided
        if (employeeId != null) {

            leaveRecords = leaveRepository.findByEmployeeId(
                    employeeId,
                    pageable
            );

        } else {

            // Retrieve all leave requests
            leaveRecords = leaveRepository.findAll(pageable);
        }

        // Convert entity records to response DTOs
        return leaveRecords.map(this::mapToResponses);
    }

    private LeaveResponse mapToResponse(LeaveRecords leaveRecord) {
        LeaveResponse leaveResponse = new LeaveResponse();
        leaveResponse.setEmployeeId(leaveRecord.getEmployeeId());
        leaveResponse.setStartDate(leaveRecord.getStartDate());
        leaveResponse.setEndDate(leaveRecord.getEndDate());
        leaveResponse.setLeaveDays(leaveRecord.getLeaveDays());
        leaveResponse.setLeaveReason(leaveRecord.getReason());
        leaveResponse.setStatus(leaveRecord.getStatus());

        return leaveResponse;
    }

    private LeaveResponse mapToResponses(LeaveRecords leaveRecord) {
        LeaveResponse leaveResponse = new LeaveResponse();
        leaveResponse.setEmployeeId(leaveRecord.getEmployeeId());
        leaveResponse.setStartDate(leaveRecord.getStartDate());
        leaveResponse.setEndDate(leaveRecord.getEndDate());
        leaveResponse.setLeaveReason(leaveRecord.getReason());
        leaveResponse.setStatus(leaveRecord.getStatus());
        leaveResponse.setManagerId(leaveRecord.getManagerId());
        leaveResponse.setDecisionComment(leaveRecord.getDecisionComment());
        leaveResponse.setDataInitiated(leaveRecord.getDataInitiated());

        return leaveResponse;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) throws InvalidDateRequestException {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRequestException("Start date cannot be after and date");
        }
        ;

        if (startDate.isBefore(LocalDate.now())) {
            throw new DateInThePastException("Start date cannot be in the past");
        }

        if (endDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("End date cannot be in the past");
        }
    }

    private int calculateLeaveDays(LocalDate startDate, LocalDate endDate) {

        int workingDays = 0;

        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return workingDays;
    }

}
