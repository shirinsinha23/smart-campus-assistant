package com.example.smartcampusassistant.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveDTO {
    private Long id;
    private String title;
    private String description;
    private String leaveType;
    private String leaveTypeDisplay;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private String status;
    private String statusDisplay;
    private Long userId;
    private String userName;
    private String userLoginId;
    private Long approvedById;
    private String approvedByName;
    private String documentUrl;
    private String documentName;
    private String reasonForRejection;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
}