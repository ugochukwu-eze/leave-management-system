package org.example.crown.leavemanagementassessment.service;

import org.example.crown.leavemanagementassessment.dto.LeaveDecisionDto;
import org.example.crown.leavemanagementassessment.dto.LeaveRequest;
import org.example.crown.leavemanagementassessment.dto.LeaveResponse;
import org.example.crown.leavemanagementassessment.exception.InvalidDateRequestException;
import org.springframework.data.domain.Page;

public interface LeaveService {

    LeaveResponse submitRequest(LeaveRequest request) throws InvalidDateRequestException;

    LeaveResponse retrieveRequestById(Long id);

    LeaveResponse decideLeaveRequest(Long id, LeaveDecisionDto leaveDecisionDto);

    Page<LeaveResponse> getLeaveRequests(Long employeeId, int page, int size);
}
