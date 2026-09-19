package com.example.smartcampusassistant.timetable.dto;

import com.example.smartcampusassistant.attendance.Subject;
import com.example.smartcampusassistant.timetable.Day;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableSummaryResponse {
    private Long totalClasses;
    private Long todayClasses;
    private Map<Day, Long> classesByDay;
    private Map<Subject, Long> classesBySubject;
}