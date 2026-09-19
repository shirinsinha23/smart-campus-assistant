package com.example.smartcampusassistant.attendance.dto;

import com.example.smartcampusassistant.attendance.AttendanceStatus;
import com.example.smartcampusassistant.attendance.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Subject subject;
    private LocalDate date;
    private AttendanceStatus status;
    private String markedByName;
}