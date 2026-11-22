package org.example.crown.leavemanagementassessment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.crown.leavemanagementassessment.enums.Decision;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveDecisionDto {

    @NotNull
    Decision decision;

    @NotNull
    String managerId;

    String comment;
}
