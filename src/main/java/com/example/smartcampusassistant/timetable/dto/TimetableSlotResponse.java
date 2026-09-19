package com.example.smartcampusassistant.timetable.dto;

import com.example.smartcampusassistant.attendance.Subject;
import com.example.smartcampusassistant.timetable.Day;
import com.example.smartcampusassistant.timetable.Period;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableSlotResponse {
    private Long id;
    private Day day;
    private Period period;
    private Subject subject;
    private Long facultyId;
    private String facultyName;
    private String room;

    // ✅ NEW FIELDS
    private String block;
    private String startTime;
    private String endTime;
}