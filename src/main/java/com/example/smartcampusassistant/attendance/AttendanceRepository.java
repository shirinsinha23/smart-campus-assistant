package com.example.smartcampusassistant.attendance;

import com.example.smartcampusassistant.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByStudentId(Long studentId);

    List<AttendanceRecord> findByStudentIdAndSubject(Long studentId, Subject subject);

    List<AttendanceRecord> findBySubjectAndDate(Subject subject, LocalDate date);

    Optional<AttendanceRecord> findByStudentIdAndSubjectAndDate(Long studentId, Subject subject, LocalDate date);
}