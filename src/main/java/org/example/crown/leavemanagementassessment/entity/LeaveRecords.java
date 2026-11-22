package org.example.crown.leavemanagementassessment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.crown.leavemanagementassessment.enums.LeaveStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class LeaveRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int leaveDays;

    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private String managerId;

    private String decisionComment;

    private LocalDateTime decidedAt;

    @CreationTimestamp
    private LocalDateTime dataInitiated;
}
