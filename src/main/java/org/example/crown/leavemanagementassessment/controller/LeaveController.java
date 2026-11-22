package org.example.crown.leavemanagementassessment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.crown.leavemanagementassessment.dto.LeaveDecisionDto;
import org.example.crown.leavemanagementassessment.dto.LeaveRequest;
import org.example.crown.leavemanagementassessment.dto.LeaveResponse;
import org.example.crown.leavemanagementassessment.exception.InvalidDateRequestException;
import org.example.crown.leavemanagementassessment.service.LeaveService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/test")
    public String firstTest() {
        return "Hello-world";
    }

    @PostMapping
    private ResponseEntity<LeaveResponse> submitRequest(
            @Valid @RequestBody LeaveRequest request
    ) throws InvalidDateRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.submitRequest(request));
    }

    @GetMapping("/{id}")
    private ResponseEntity<LeaveResponse> retrieveRequestById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(leaveService.retrieveRequestById(id));
    }

    @PatchMapping("/{id}/decision")
    public ResponseEntity<LeaveResponse> decideLeaveRequest(
            @PathVariable Long id,
            @Valid @RequestBody LeaveDecisionDto decisionDto) {

        LeaveResponse response =
                leaveService.decideLeaveRequest(id, decisionDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<LeaveResponse>> getLeaveRequests(
            @RequestParam(required = false)
            Long employeeId, @RequestParam(defaultValue = "0")
            int page, @RequestParam(defaultValue = "10")
            int size) {

        Page<LeaveResponse> response =
                leaveService.getLeaveRequests(employeeId, page, size);

        return ResponseEntity.ok(response);
    }
}
