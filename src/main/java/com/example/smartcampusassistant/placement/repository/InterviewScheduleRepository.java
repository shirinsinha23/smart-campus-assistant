package com.example.smartcampusassistant.placement.repository;

import com.example.smartcampusassistant.placement.entity.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

    List<InterviewSchedule> findByApplicationId(Long applicationId);

    List<InterviewSchedule> findByStatus(String status);

    @Query("SELECT is FROM InterviewSchedule is WHERE is.application.student.id = :studentId")
    List<InterviewSchedule> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT is FROM InterviewSchedule is WHERE is.interviewDate = :date AND is.status = 'SCHEDULED'")
    List<InterviewSchedule> findScheduledByDate(@Param("date") LocalDate date);

    // 👇 ADD THESE MISSING METHODS
    @Query("SELECT COUNT(is) FROM InterviewSchedule is WHERE is.status = 'SCHEDULED' AND is.interviewDate BETWEEN :startDate AND :endDate")
    long countScheduledThisWeek(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT is.status, COUNT(is) FROM InterviewSchedule is GROUP BY is.status")
    List<Object[]> countInterviewsByStatus();

    @Query("SELECT is FROM InterviewSchedule is WHERE is.application.student.id = :studentId AND is.status = 'SCHEDULED'")
    List<InterviewSchedule> findUpcomingByStudentId(@Param("studentId") Long studentId);
}