package com.example.smartcampusassistant.attendance;

import com.example.smartcampusassistant.attendance.dto.AttendancePercentageResponse;
import com.example.smartcampusassistant.attendance.dto.AttendanceResponse;
import com.example.smartcampusassistant.attendance.dto.AttendanceSummaryResponse;
import com.example.smartcampusassistant.attendance.dto.MarkAttendanceRequest;
import com.example.smartcampusassistant.exception.ResourceNotFoundException;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    /**
     * Marks (or re-marks) attendance for the given students in a subject on a date.
     *
     * Upsert behaviour:
     *   - If a record already exists for (studentId, subject, date), its status is updated.
     *   - Otherwise a new record is created.
     *
     * This means calling mark-attendance twice for the same day updates the values
     * instead of throwing a 400 "already marked" error.
     */
    public List<AttendanceResponse> markAttendance(MarkAttendanceRequest request, String facultyEmail) {
        User faculty = userRepository.findByEmail(facultyEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with email: " + facultyEmail));

        System.out.println("✅ Faculty found: " + faculty.getName() + " (Login ID: " + faculty.getLoginId() + ")");

        List<AttendanceRecord> records = request.getStudents().stream()
                .map(entry -> {
                    User student = userRepository.findById(entry.getStudentId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Student not found with id: " + entry.getStudentId()));

                    // Upsert: reuse existing record if present, otherwise build a new one
                    AttendanceRecord record = attendanceRepository
                            .findByStudentIdAndSubjectAndDate(
                                    entry.getStudentId(), request.getSubject(), request.getDate())
                            .orElseGet(() -> AttendanceRecord.builder()
                                    .student(student)
                                    .markedBy(faculty)
                                    .subject(request.getSubject())
                                    .date(request.getDate())
                                    .build());

                    // Always apply the latest status and marker
                    record.setStatus(entry.getStatus());
                    record.setMarkedBy(faculty);

                    return record;
                })
                .collect(Collectors.toList());

        List<AttendanceRecord> saved = attendanceRepository.saveAll(records);

        return saved.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AttendanceResponse> getAttendanceForStudent(Long studentId) {
        List<AttendanceRecord> records = attendanceRepository.findByStudentId(studentId);
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AttendanceResponse> getAttendanceForStudentBySubject(Long studentId, Subject subject) {
        List<AttendanceRecord> records = attendanceRepository.findByStudentIdAndSubject(studentId, subject);
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AttendancePercentageResponse getAttendancePercentage(Long studentId, Subject subject) {
        List<AttendanceRecord> records = attendanceRepository.findByStudentIdAndSubject(studentId, subject);

        long total = records.size();
        long present = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                .count();

        double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

        return AttendancePercentageResponse.builder()
                .subject(subject)
                .totalClasses(total)
                .presentCount(present)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    public List<AttendanceResponse> getAllAttendance() {
        List<AttendanceRecord> records = attendanceRepository.findAll();
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AttendanceSummaryResponse getAttendanceSummary(Long studentId) {
        List<AttendanceRecord> records = attendanceRepository.findByStudentId(studentId);

        long total = records.size();
        long present = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                .count();

        double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

        Map<Subject, AttendanceSummaryResponse.SubjectAttendance> subjectBreakdown = new HashMap<>();
        for (Subject subject : Subject.values()) {
            List<AttendanceRecord> subjectRecords = records.stream()
                    .filter(r -> r.getSubject() == subject)
                    .collect(Collectors.toList());

            long subjectTotal = subjectRecords.size();
            long subjectPresent = subjectRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                    .count();

            double subjectPercentage = subjectTotal == 0 ? 0.0 : (subjectPresent * 100.0) / subjectTotal;

            subjectBreakdown.put(subject, new AttendanceSummaryResponse.SubjectAttendance(
                    subjectTotal, subjectPresent, Math.round(subjectPercentage * 100.0) / 100.0
            ));
        }

        return AttendanceSummaryResponse.builder()
                .totalClasses(total)
                .presentCount(present)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .subjectBreakdown(subjectBreakdown)
                .build();
    }

    private AttendanceResponse toResponse(AttendanceRecord record) {
        return AttendanceResponse.builder()
                .id(record.getId())
                .studentId(record.getStudent().getId())
                .studentName(record.getStudent().getName())
                .subject(record.getSubject())
                .date(record.getDate())
                .status(record.getStatus())
                .markedByName(record.getMarkedBy().getName())
                .build();
    }
}