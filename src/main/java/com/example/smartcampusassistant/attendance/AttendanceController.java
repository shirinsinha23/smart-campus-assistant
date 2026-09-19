package com.example.smartcampusassistant.attendance;

import com.example.smartcampusassistant.attendance.dto.AttendancePercentageResponse;
import com.example.smartcampusassistant.attendance.dto.AttendanceResponse;
import com.example.smartcampusassistant.attendance.dto.AttendanceSummaryResponse;
import com.example.smartcampusassistant.attendance.dto.MarkAttendanceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request,
            Authentication authentication
    ) {
        try {
            // ✅ Get email from authentication (JWT uses email as username)
            String facultyEmail = authentication.getName();
            System.out.println("📝 Faculty Email: " + facultyEmail);
            System.out.println("📚 Subject: " + request.getSubject());
            System.out.println("📅 Date: " + request.getDate());
            System.out.println("👨‍🎓 Students: " + request.getStudents().size());

            List<AttendanceResponse> response = attendanceService.markAttendance(request, facultyEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error marking attendance: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForStudent(studentId));
    }

    @GetMapping("/student/{studentId}/subject/{subject}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendanceBySubject(
            @PathVariable Long studentId,
            @PathVariable Subject subject
    ) {
        return ResponseEntity.ok(attendanceService.getAttendanceForStudentBySubject(studentId, subject));
    }

    @GetMapping("/student/{studentId}/subject/{subject}/percentage")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<AttendancePercentageResponse> getAttendancePercentage(
            @PathVariable Long studentId,
            @PathVariable Subject subject
    ) {
        return ResponseEntity.ok(attendanceService.getAttendancePercentage(studentId, subject));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/student/{studentId}/summary")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<AttendanceSummaryResponse> getAttendanceSummary(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceSummary(studentId));
    }
}