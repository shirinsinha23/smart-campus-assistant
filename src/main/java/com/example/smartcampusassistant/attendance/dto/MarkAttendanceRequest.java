package com.example.smartcampusassistant.attendance.dto;

import com.example.smartcampusassistant.attendance.AttendanceStatus;
import com.example.smartcampusassistant.attendance.Subject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MarkAttendanceRequest {

    @NotNull(message = "Subject is required")
    private Subject subject;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotEmpty(message = "Student attendance list cannot be empty")
    @Valid
    private List<StudentAttendanceEntry> students;

    @Data
    public static class StudentAttendanceEntry {

        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Status is required")
        private AttendanceStatus status;
    }
}