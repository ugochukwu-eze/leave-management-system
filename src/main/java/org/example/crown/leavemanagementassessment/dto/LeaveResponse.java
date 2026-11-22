package org.example.crown.leavemanagementassessment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.example.crown.leavemanagementassessment.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveResponse {

    private Long employeeId;

    private LocalDate startDate;

    private LocalDate endDate;

    private int leaveDays;

    private String leaveReason;

    private org.example.crown.leavemanagementassessment.enums.LeaveStatus status;

    private String managerId;

    private String decisionComment;

    private LocalDateTime decidedAt;

    private LocalDateTime dataInitiated;
}
