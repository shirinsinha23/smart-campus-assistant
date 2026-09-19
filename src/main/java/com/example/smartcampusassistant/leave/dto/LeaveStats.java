package com.example.smartcampusassistant.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveStats {
    private long total;
    private long pending;
    private long approved;
    private long rejected;
    private long cancelled;
    private int totalUsedDays;
}