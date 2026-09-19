package com.example.smartcampusassistant.leave.service;

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
import com.example.smartcampusassistant.leave.repository.LeaveRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;

    private static final int MAX_ANNUAL_LEAVES = 20;

    @Transactional
    public LeaveDTO applyLeave(Long userId, LeaveRequest request) {
        log.info("📝 Applying leave for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new LeaveNotFoundException("User not found with id: " + userId));

        validateLeaveDates(request.getStartDate(), request.getEndDate());
        checkForOverlappingLeaves(userId, request.getStartDate(), request.getEndDate());

        LeaveStats stats = getLeaveStats(userId);
        int remaining = MAX_ANNUAL_LEAVES - stats.getTotalUsedDays();
        int requestedDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        if (requestedDays > remaining) {
            throw new LeaveValidationException("Insufficient leave balance. Available: " + remaining + ", Requested: " + requestedDays);
        }

        Leave leave = Leave.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .leaveType(LeaveType.valueOf(request.getLeaveType()))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .user(user)
                .status(LeaveStatus.PENDING)
                .documentUrl(request.getDocumentUrl())
                .documentName(request.getDocumentName())
                .build();

        Leave saved = leaveRepository.save(leave);
        log.info("✅ Leave applied successfully with ID: {}", saved.getId());
        return convertToDTO(saved);
    }

    public Page<LeaveDTO> getLeavesByUserWithFilters(Long userId, String status, String leaveType,
                                                     LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        log.info("📋 Fetching leaves for user: {} with filters", userId);

        if (!userRepository.existsById(userId)) {
            throw new LeaveNotFoundException("User not found with id: " + userId);
        }

        Page<Leave> leavePage;

        if (status != null && !status.isEmpty()) {
            LeaveStatus leaveStatus = LeaveStatus.valueOf(status.toUpperCase());
            if (leaveType != null && !leaveType.isEmpty()) {
                LeaveType type = LeaveType.valueOf(leaveType.toUpperCase());
                leavePage = leaveRepository.findByUserIdAndStatusAndLeaveType(userId, leaveStatus, type, pageable);
            } else {
                leavePage = leaveRepository.findByUserIdAndStatus(userId, leaveStatus, pageable);
            }
        } else if (leaveType != null && !leaveType.isEmpty()) {
            LeaveType type = LeaveType.valueOf(leaveType.toUpperCase());
            leavePage = leaveRepository.findByUserIdAndLeaveType(userId, type, pageable);
        } else if (fromDate != null && toDate != null) {
            leavePage = leaveRepository.findByUserIdAndDateRange(userId, fromDate, toDate, pageable);
        } else {
            leavePage = leaveRepository.findByUserId(userId, pageable);
        }

        return leavePage.map(this::convertToDTO);
    }

    public Page<LeaveDTO> getPendingLeaves(Pageable pageable) {
        log.info("📋 Fetching all pending leaves");
        Page<Leave> leavePage = leaveRepository.findByStatus(LeaveStatus.PENDING, pageable);
        return leavePage.map(this::convertToDTO);
    }

    public Page<LeaveDTO> getAllLeavesWithFilters(String status, String leaveType, String search, Pageable pageable) {
        log.info("📋 Fetching all leaves with filters");

        if (search != null && !search.isEmpty()) {
            Page<Leave> leavePage = leaveRepository.searchLeaves(search, pageable);
            return leavePage.map(this::convertToDTO);
        }

        if (status != null && !status.isEmpty()) {
            LeaveStatus leaveStatus = LeaveStatus.valueOf(status.toUpperCase());
            if (leaveType != null && !leaveType.isEmpty()) {
                LeaveType type = LeaveType.valueOf(leaveType.toUpperCase());
                Page<Leave> leavePage = leaveRepository.findByStatusAndLeaveType(leaveStatus, type, pageable);
                return leavePage.map(this::convertToDTO);
            }
            Page<Leave> leavePage = leaveRepository.findByStatus(leaveStatus, pageable);
            return leavePage.map(this::convertToDTO);
        }

        if (leaveType != null && !leaveType.isEmpty()) {
            LeaveType type = LeaveType.valueOf(leaveType.toUpperCase());
            Page<Leave> leavePage = leaveRepository.findByLeaveType(type, pageable);
            return leavePage.map(this::convertToDTO);
        }

        Page<Leave> leavePage = leaveRepository.findAll(pageable);
        return leavePage.map(this::convertToDTO);
    }

    public LeaveDTO getLeaveById(Long leaveId) {
        log.info("📋 Fetching leave by ID: {}", leaveId);
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException(leaveId));
        return convertToDTO(leave);
    }

    @Transactional
    public LeaveDTO approveLeave(Long leaveId, Long approverId, String comments) {
        log.info("📝 Approving leave: {} by approver: {}", leaveId, approverId);

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException(leaveId));

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new LeaveNotFoundException("Approver not found with id: " + approverId));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveValidationException("Leave is not in pending state. Current status: " + leave.getStatus());
        }

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setApprovedBy(approver);
        leave.setApprovedAt(LocalDateTime.now());
        if (comments != null && !comments.isEmpty()) {
            leave.setReasonForRejection(comments);
        }

        Leave saved = leaveRepository.save(leave);
        log.info("✅ Leave approved successfully: {}", saved.getId());
        return convertToDTO(saved);
    }

    @Transactional
    public LeaveDTO rejectLeave(Long leaveId, String reason) {
        log.info("📝 Rejecting leave: {} reason: {}", leaveId, reason);

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException(leaveId));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveValidationException("Leave is not in pending state. Current status: " + leave.getStatus());
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setReasonForRejection(reason);

        Leave saved = leaveRepository.save(leave);
        log.info("✅ Leave rejected successfully: {}", saved.getId());
        return convertToDTO(saved);
    }

    @Transactional
    public void cancelLeave(Long leaveId, Long userId) {
        log.info("📝 Cancelling leave: {} by user: {}", leaveId, userId);

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException(leaveId));

        if (!leave.getUser().getId().equals(userId)) {
            throw new UnauthorizedLeaveActionException("You are not authorized to cancel this leave");
        }

        if (leave.getStatus() == LeaveStatus.APPROVED) {
            throw new LeaveValidationException("Cannot cancel an approved leave. Please contact administrator.");
        }

        if (leave.getStatus() == LeaveStatus.REJECTED) {
            throw new LeaveValidationException("Cannot cancel a rejected leave.");
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        leaveRepository.save(leave);
        log.info("✅ Leave cancelled successfully: {}", leave.getId());
    }

    public LeaveStats getLeaveStats(Long userId) {
        log.info("📋 Getting leave stats for user: {}", userId);

        List<Leave> allLeaves = leaveRepository.findByUserId(userId);
        long pending = allLeaves.stream().filter(l -> l.getStatus() == LeaveStatus.PENDING).count();
        long approved = allLeaves.stream().filter(l -> l.getStatus() == LeaveStatus.APPROVED).count();
        long rejected = allLeaves.stream().filter(l -> l.getStatus() == LeaveStatus.REJECTED).count();
        long cancelled = allLeaves.stream().filter(l -> l.getStatus() == LeaveStatus.CANCELLED).count();

        int totalUsedDays = allLeaves.stream()
                .filter(l -> l.getStatus() == LeaveStatus.APPROVED)
                .filter(l -> l.getStartDate().getYear() == LocalDate.now().getYear())
                .mapToInt(Leave::getTotalDays)
                .sum();

        return LeaveStats.builder()
                .total(allLeaves.size())
                .pending(pending)
                .approved(approved)
                .rejected(rejected)
                .cancelled(cancelled)
                .totalUsedDays(totalUsedDays)
                .build();
    }

    public LeaveSummaryResponse getLeaveSummary(Long userId) {
        log.info("📋 Getting leave summary for user: {}", userId);

        LeaveStats stats = getLeaveStats(userId);
        int remaining = MAX_ANNUAL_LEAVES - stats.getTotalUsedDays();

        List<LeaveDTO> recentLeaves = leaveRepository.findByUserId(userId, PageRequest.of(0, 5))
                .map(this::convertToDTO)
                .getContent();

        return LeaveSummaryResponse.builder()
                .userId(userId)
                .totalLeaves(stats.getTotal())
                .pendingLeaves(stats.getPending())
                .approvedLeaves(stats.getApproved())
                .rejectedLeaves(stats.getRejected())
                .cancelledLeaves(stats.getCancelled())
                .totalUsedDays(stats.getTotalUsedDays())
                .remainingLeaves(remaining)
                .maxAnnualLeaves(MAX_ANNUAL_LEAVES)
                .recentLeaves(recentLeaves)
                .build();
    }

    public List<Map<String, String>> getLeaveTypes() {
        return Arrays.stream(LeaveType.values())
                .map(type -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("value", type.name());
                    map.put("label", type.getDisplayName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<LeaveDTO> getLeavesByDateRange(LocalDate startDate, LocalDate endDate, String status) {
        log.info("📋 Getting leaves between {} and {}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            throw new LeaveValidationException("Start date must be before end date");
        }

        List<Leave> leaves;
        if (status != null && !status.isEmpty()) {
            LeaveStatus leaveStatus = LeaveStatus.valueOf(status.toUpperCase());
            leaves = leaveRepository.findByDateRangeAndStatus(startDate, endDate, leaveStatus);
        } else {
            leaves = leaveRepository.findByDateRange(startDate, endDate);
        }

        return leaves.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Map<String, Object> getLeaveCalendarData(LocalDate startDate, LocalDate endDate) {
        log.info("📋 Getting leave calendar data from {} to {}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            throw new LeaveValidationException("Start date must be before end date");
        }

        List<Leave> approvedLeaves = leaveRepository.findApprovedLeavesInDateRange(startDate, endDate);

        Map<String, List<LeaveDTO>> leavesByDate = approvedLeaves.stream()
                .map(this::convertToDTO)
                .collect(Collectors.groupingBy(leave -> leave.getStartDate().toString()));

        Map<String, Object> response = new HashMap<>();
        response.put("leavesByDate", leavesByDate);
        response.put("totalLeaves", approvedLeaves.size());
        response.put("startDate", startDate);
        response.put("endDate", endDate);

        return response;
    }

    @Transactional
    public List<LeaveDTO> bulkApproveLeaves(List<Long> leaveIds, Long approverId) {
        log.info("📝 Bulk approving {} leaves", leaveIds.size());

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new LeaveNotFoundException("Approver not found with id: " + approverId));

        List<LeaveDTO> approvedLeaves = new ArrayList<>();

        for (Long leaveId : leaveIds) {
            try {
                Leave leave = leaveRepository.findById(leaveId)
                        .orElseThrow(() -> new LeaveNotFoundException(leaveId));

                if (leave.getStatus() == LeaveStatus.PENDING) {
                    leave.setStatus(LeaveStatus.APPROVED);
                    leave.setApprovedBy(approver);
                    leave.setApprovedAt(LocalDateTime.now());
                    leaveRepository.save(leave);
                    approvedLeaves.add(convertToDTO(leave));
                }
            } catch (Exception e) {
                log.error("Error approving leave {}: {}", leaveId, e.getMessage());
            }
        }

        return approvedLeaves;
    }

    public List<LeaveDTO> getLeavesOnDate(LocalDate date) {
        log.info("📋 Getting leaves on date: {}", date);
        List<Leave> leaves = leaveRepository.findApprovedLeavesOnDate(date);
        return leaves.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public boolean isUserOnLeave(Long userId, LocalDate date) {
        log.info("🔍 Checking if user {} is on leave on {}", userId, date);
        List<Leave> leaves = leaveRepository.findApprovedLeavesByUserOnDate(userId, date);
        return !leaves.isEmpty();
    }

    private void validateLeaveDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new LeaveValidationException("Start date must be before or equal to end date");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new LeaveValidationException("Cannot apply for leave in the past");
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days > 30) {
            throw new LeaveValidationException("Leave duration cannot exceed 30 days");
        }
    }

    private void checkForOverlappingLeaves(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Leave> overlappingLeaves = leaveRepository.findOverlappingLeaves(userId, startDate, endDate);
        if (!overlappingLeaves.isEmpty()) {
            throw new LeaveValidationException("You already have an approved or pending leave in this period");
        }
    }

    private LeaveDTO convertToDTO(Leave leave) {
        return LeaveDTO.builder()
                .id(leave.getId())
                .title(leave.getTitle())
                .description(leave.getDescription())
                .leaveType(leave.getLeaveType().name())
                .leaveTypeDisplay(leave.getLeaveType().getDisplayName())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .totalDays(leave.getTotalDays())
                .status(leave.getStatus().name())
                .statusDisplay(leave.getStatus().getDisplayName())
                .userId(leave.getUser().getId())
                .userName(leave.getUser().getName())
                .userLoginId(leave.getUser().getLoginId())
                .approvedById(leave.getApprovedBy() != null ? leave.getApprovedBy().getId() : null)
                .approvedByName(leave.getApprovedBy() != null ? leave.getApprovedBy().getName() : null)
                .documentUrl(leave.getDocumentUrl())
                .documentName(leave.getDocumentName())
                .reasonForRejection(leave.getReasonForRejection())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .approvedAt(leave.getApprovedAt())
                .build();
    }
}