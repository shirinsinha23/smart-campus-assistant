package com.example.smartcampusassistant.timetable;

import com.example.smartcampusassistant.attendance.Subject;
import com.example.smartcampusassistant.exception.BadRequestException;
import com.example.smartcampusassistant.exception.ResourceNotFoundException;
import com.example.smartcampusassistant.timetable.dto.CreateSlotRequest;
import com.example.smartcampusassistant.timetable.dto.TimetableSlotResponse;
import com.example.smartcampusassistant.timetable.dto.TimetableSummaryResponse;
import com.example.smartcampusassistant.user.Role;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final UserRepository userRepository;

    public TimetableSlotResponse createSlot(CreateSlotRequest request) {
        User faculty = userRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Faculty not found with id: " + request.getFacultyId()));

        if (faculty.getRole() != Role.FACULTY) {
            throw new BadRequestException(
                    "User with id " + request.getFacultyId() + " is not a FACULTY member");
        }

        // Prevent double-booking the same day+period
        timetableRepository.findByDayAndPeriod(request.getDay(), request.getPeriod())
                .ifPresent(existing -> {
                    throw new BadRequestException(
                            "Slot already occupied: " + request.getDay() + " " + request.getPeriod());
                });

        // Prevent the same faculty being double-booked at the same day+period
        timetableRepository.findByFacultyIdAndDayAndPeriod(
                request.getFacultyId(), request.getDay(), request.getPeriod()
        ).ifPresent(existing -> {
            throw new BadRequestException(
                    "Faculty is already assigned to another class at " +
                            request.getDay() + " " + request.getPeriod());
        });

        // Get times from period if not provided
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();
        if (startTime == null || endTime == null) {
            // Default times based on period
            switch (request.getPeriod()) {
                case PERIOD_1: startTime = "09:00"; endTime = "10:00"; break;
                case PERIOD_2: startTime = "10:00"; endTime = "11:00"; break;
                case PERIOD_3: startTime = "11:00"; endTime = "12:00"; break;
                case PERIOD_4: startTime = "12:00"; endTime = "13:00"; break;
                case PERIOD_5: startTime = "14:00"; endTime = "15:00"; break;
                case PERIOD_6: startTime = "15:00"; endTime = "16:00"; break;
                default: startTime = "09:00"; endTime = "10:00";
            }
        }

        // Set default block if not provided
        String block = request.getBlock() != null ? request.getBlock() : "Block 1";

        TimetableSlot slot = TimetableSlot.builder()
                .day(request.getDay())
                .period(request.getPeriod())
                .subject(request.getSubject())
                .faculty(faculty)
                .room(request.getRoom())
                .block(block)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        TimetableSlot saved = timetableRepository.save(slot);
        return toResponse(saved);
    }

    public List<TimetableSlotResponse> getFullTimetable() {
        List<TimetableSlot> slots = timetableRepository.findAllByOrderByDayAscPeriodAsc();
        return slots.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TimetableSlotResponse> getFacultyTimetable(Long facultyId) {
        List<TimetableSlot> slots = timetableRepository.findByFacultyId(facultyId);
        return slots.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deleteSlot(Long slotId) {
        TimetableSlot slot = timetableRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable slot not found with id: " + slotId));
        timetableRepository.delete(slot);
    }

    // ==================== NEW METHODS ====================

    public TimetableSummaryResponse getTimetableSummary() {
        List<TimetableSlot> slots = timetableRepository.findAll();

        // Group by day
        Map<Day, Long> classesByDay = slots.stream()
                .collect(Collectors.groupingBy(TimetableSlot::getDay, Collectors.counting()));

        // Group by subject
        Map<Subject, Long> classesBySubject = slots.stream()
                .collect(Collectors.groupingBy(TimetableSlot::getSubject, Collectors.counting()));

        // Get today's classes
        String today = LocalDate.now().getDayOfWeek().toString();
        Day todayDay = Day.valueOf(today);
        long todayClasses = slots.stream()
                .filter(slot -> slot.getDay() == todayDay)
                .count();

        return TimetableSummaryResponse.builder()
                .totalClasses((long) slots.size())
                .todayClasses(todayClasses)
                .classesByDay(classesByDay)
                .classesBySubject(classesBySubject)
                .build();
    }

    public List<TimetableSlotResponse> getTodaySchedule(Long facultyId) {
        String today = LocalDate.now().getDayOfWeek().toString();
        Day todayDay = Day.valueOf(today);

        List<TimetableSlot> slots;
        if (facultyId != null) {
            slots = timetableRepository.findByFacultyIdAndDay(facultyId, todayDay);
        } else {
            slots = timetableRepository.findByDay(todayDay);
        }

        return slots.stream()
                .sorted(Comparator.comparing(TimetableSlot::getPeriod))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TimetableSlotResponse toResponse(TimetableSlot slot) {
        return TimetableSlotResponse.builder()
                .id(slot.getId())
                .day(slot.getDay())
                .period(slot.getPeriod())
                .subject(slot.getSubject())
                .facultyId(slot.getFaculty().getId())
                .facultyName(slot.getFaculty().getName())
                .room(slot.getRoom())
                .block(slot.getBlock())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .build();
    }
}