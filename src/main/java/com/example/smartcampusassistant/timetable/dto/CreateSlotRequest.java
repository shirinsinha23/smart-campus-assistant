package com.example.smartcampusassistant.timetable.dto;

import com.example.smartcampusassistant.attendance.Subject;
import com.example.smartcampusassistant.timetable.Day;
import com.example.smartcampusassistant.timetable.Period;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSlotRequest {

    @NotNull(message = "Day is required")
    private Day day;

    @NotNull(message = "Period is required")
    private Period period;

    @NotNull(message = "Subject is required")
    private Subject subject;

    @NotNull(message = "Faculty ID is required")
    private Long facultyId;

    private String room;

    // ✅ NEW FIELDS
    private String block;
    private String startTime;
    private String endTime;
}