package com.example.smartcampusassistant.hostel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorLogDTO {
    private Long id;
    private String visitorName;
    private String visitorPhone;
    private String visitorId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String purpose;
    private Long studentId;
    private String studentName;
    private Long roomId;
    private String roomNumber;
    private String remarks;
    private Boolean isActive;
}