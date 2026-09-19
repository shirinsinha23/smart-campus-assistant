package com.example.smartcampusassistant.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {
    private String title;
    private String description;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String documentUrl;
    private String documentName;
}