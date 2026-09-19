package com.example.smartcampusassistant.leave.controller;

import com.example.smartcampusassistant.leave.dto.LeaveDTO;
import com.example.smartcampusassistant.leave.dto.LeaveRequest;
import com.example.smartcampusassistant.leave.dto.LeaveStats;
import com.example.smartcampusassistant.leave.dto.LeaveSummaryResponse;
import com.example.smartcampusassistant.leave.entity.Leave;
import com.example.smartcampusassistant.leave.enums.LeaveStatus;
import com.example.smartcampusassistant.leave.enums.LeaveType;
import com.example.smartcampusassistant.leave.exception.LeaveNotFoundException;
import com.example.smartcampusassistant.leave.exception.LeaveValidationException;
import com.example.smartcampusassistant.leave.exception.UnauthorizedLeaveActionException;
import com.example.smartcampusassistant.leave.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Slf4j
public class LeaveController {

    private final LeaveService leaveService;

    @Value("${app.leave.upload.dir}")
    private String leaveUploadDir;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LeaveDTO> applyLeave(
            @RequestParam Long userId,
            @RequestPart("leave") @Valid LeaveRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        log.info("📝 Apply leave for user {} (file: {})",
                userId, file == null ? "none" : file.getOriginalFilename());

        if (file != null && !file.isEmpty()) {
            String original = file.getOriginalFilename();
            String stored = UUID.randomUUID() + "_" +
                    (original == null ? "proof" : original);

            Path dir = Paths.get(leaveUploadDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(stored);
            file.transferTo(target.toFile());

            request.setDocumentUrl("/uploads/leaves/" + stored);
            request.setDocumentName(original);
            log.info("📎 Leave proof saved: {}", target.toAbsolutePath());
        }

        LeaveDTO leave = leaveService.applyLeave(userId, request);
        return new ResponseEntity<>(leave, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getLeavesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String leaveType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        log.info("📋 Get leaves for user: {}", userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LeaveDTO> leavePage = leaveService.getLeavesByUserWithFilters(userId, status, leaveType, fromDate, toDate, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("leaves", leavePage.getContent());
        response.put("currentPage", leavePage.getNumber());
        response.put("totalItems", leavePage.getTotalElements());
        response.put("totalPages", leavePage.getTotalPages());
        response.put("size", leavePage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getPendingLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("📋 Get all pending leaves");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startDate"));
        Page<LeaveDTO> leavePage = leaveService.getPendingLeaves(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("leaves", leavePage.getContent());
        response.put("currentPage", leavePage.getNumber());
        response.put("totalItems", leavePage.getTotalElements());
        response.put("totalPages", leavePage.getTotalPages());
        response.put("size", leavePage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Map<String, Object>> getAllLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String leaveType,
            @RequestParam(required = false) String search) {
        log.info("📋 Get all leaves");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LeaveDTO> leavePage = leaveService.getAllLeavesWithFilters(status, leaveType, search, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("leaves", leavePage.getContent());
        response.put("currentPage", leavePage.getNumber());
        response.put("totalItems", leavePage.getTotalElements());
        response.put("totalPages", leavePage.getTotalPages());
        response.put("size", leavePage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveDTO> getLeaveById(@PathVariable Long leaveId) {
        log.info("📋 Get leave by ID: {}", leaveId);
        return ResponseEntity.ok(leaveService.getLeaveById(leaveId));
    }

    @PutMapping("/{leaveId}/approve")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<LeaveDTO> approveLeave(
            @PathVariable Long leaveId,
            @RequestParam Long approverId,
            @RequestParam(required = false) String comments) {
        log.info("📝 Approve leave: {} by approver: {}", leaveId, approverId);
        LeaveDTO leave = leaveService.approveLeave(leaveId, approverId, comments);
        return ResponseEntity.ok(leave);
    }

    @PutMapping("/{leaveId}/reject")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<LeaveDTO> rejectLeave(
            @PathVariable Long leaveId,
            @RequestParam String reason) {
        log.info("📝 Reject leave: {}", leaveId);
        LeaveDTO leave = leaveService.rejectLeave(leaveId, reason);
        return ResponseEntity.ok(leave);
    }

    @DeleteMapping("/{leaveId}")
    public ResponseEntity<Map<String, String>> cancelLeave(
            @PathVariable Long leaveId,
            @RequestParam Long userId) {
        log.info("📝 Cancel leave: {} by user: {}", leaveId, userId);
        leaveService.cancelLeave(leaveId, userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Leave cancelled successfully");
        response.put("leaveId", leaveId.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<LeaveStats> getLeaveStats(@PathVariable Long userId) {
        log.info("📋 Get leave stats for user: {}", userId);
        return ResponseEntity.ok(leaveService.getLeaveStats(userId));
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<LeaveSummaryResponse> getLeaveSummary(@PathVariable Long userId) {
        log.info("📋 Get leave summary for user: {}", userId);
        return ResponseEntity.ok(leaveService.getLeaveSummary(userId));
    }

    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getLeaveTypes() {
        log.info("📋 Get leave types");
        return ResponseEntity.ok(leaveService.getLeaveTypes());
    }
}