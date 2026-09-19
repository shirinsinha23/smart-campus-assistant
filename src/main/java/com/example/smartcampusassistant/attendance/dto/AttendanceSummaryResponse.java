package com.example.smartcampusassistant.attendance.dto;

import com.example.smartcampusassistant.attendance.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSummaryResponse {
    private long totalClasses;
    private long presentCount;
    private double percentage;
    private Map<Subject, SubjectAttendance> subjectBreakdown;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SubjectAttendance {
        private long totalClasses;
        private long presentCount;
        private double percentage;
    }
}