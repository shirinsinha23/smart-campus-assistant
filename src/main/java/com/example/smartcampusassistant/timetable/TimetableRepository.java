package com.example.smartcampusassistant.timetable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimetableRepository extends JpaRepository<TimetableSlot, Long> {

    List<TimetableSlot> findAllByOrderByDayAscPeriodAsc();

    List<TimetableSlot> findByFacultyId(Long facultyId);

    Optional<TimetableSlot> findByDayAndPeriod(Day day, Period period);

    Optional<TimetableSlot> findByFacultyIdAndDayAndPeriod(Long facultyId, Day day, Period period);

    // ==================== NEW METHODS ====================

    List<TimetableSlot> findByFacultyIdAndDay(Long facultyId, Day day);

    List<TimetableSlot> findByDay(Day day);
}