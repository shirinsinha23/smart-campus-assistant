package com.example.smartcampusassistant.timetable;

import com.example.smartcampusassistant.timetable.dto.CreateSlotRequest;
import com.example.smartcampusassistant.timetable.dto.TimetableSlotResponse;
import com.example.smartcampusassistant.timetable.dto.TimetableSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        try {
            TimetableSlotResponse response = timetableService.createSlot(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<List<TimetableSlotResponse>> getFullTimetable() {
        return ResponseEntity.ok(timetableService.getFullTimetable());
    }

    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<List<TimetableSlotResponse>> getFacultyTimetable(@PathVariable Long facultyId) {
        return ResponseEntity.ok(timetableService.getFacultyTimetable(facultyId));
    }

    @DeleteMapping("/{slotId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long slotId) {
        timetableService.deleteSlot(slotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimetableSummaryResponse> getTimetableSummary() {
        return ResponseEntity.ok(timetableService.getTimetableSummary());
    }

    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<List<TimetableSlotResponse>> getTodaySchedule(
            @RequestParam(required = false) Long facultyId) {
        return ResponseEntity.ok(timetableService.getTodaySchedule(facultyId));
    }
}