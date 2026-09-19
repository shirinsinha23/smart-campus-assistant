package com.example.smartcampusassistant.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveSummaryResponse {
    private Long userId;
    private long totalLeaves;
    private long pendingLeaves;
    private long approvedLeaves;
    private long rejectedLeaves;
    private long cancelledLeaves;
    private int totalUsedDays;
    private int remainingLeaves;
    private int maxAnnualLeaves;
    private List<LeaveDTO> recentLeaves;
}