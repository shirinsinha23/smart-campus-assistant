package com.example.smartcampusassistant.attendance.dto;

import com.example.smartcampusassistant.attendance.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendancePercentageResponse {
    private Subject subject;
    private long totalClasses;
    private long presentCount;
    private double percentage;
}