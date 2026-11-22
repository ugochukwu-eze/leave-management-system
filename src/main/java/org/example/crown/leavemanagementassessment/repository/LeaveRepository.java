package org.example.crown.leavemanagementassessment.repository;

import jakarta.persistence.LockModeType;
import org.example.crown.leavemanagementassessment.dto.LeaveRequest;
import org.example.crown.leavemanagementassessment.entity.LeaveRecords;
import org.example.crown.leavemanagementassessment.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRecords, String> {

    boolean existsByIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long id,
            Collection<LeaveStatus> statuses,
            LocalDate requestedEnd,
            LocalDate requestedStart
    );

    @Query("""
        SELECT COALESCE(SUM(l.leaveDays), 0)
        FROM LeaveRecords l
        WHERE l.employeeId = :employeeId
        AND l.status IN :statuses
    """)
    Long sumReservedDays(
            @Param("employeeId") Long employeeId,
            @Param("statuses") Collection<LeaveStatus> statuses
    );

    Optional<LeaveRecords> findLeaveRecordsById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT l FROM LeaveRecords l
    WHERE l.id = :id
""")
    Optional<LeaveRecords> findByIdForUpdate(
            @Param("id") Long id
    );


    Page<LeaveRecords>  findByEmployeeId(Long employeeId, Pageable pageable);
}
