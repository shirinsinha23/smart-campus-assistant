package com.example.smartcampusassistant.hostel.repository;

import com.example.smartcampusassistant.hostel.entity.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {

    List<VisitorLog> findByStudentId(Long studentId);

    List<VisitorLog> findByRoomId(Long roomId);

    List<VisitorLog> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);

    List<VisitorLog> findByStudentIdAndIsActiveTrue(Long studentId);

    List<VisitorLog> findByIsActiveTrue();

    @Query("SELECT COUNT(v) FROM VisitorLog v WHERE v.isActive = true")
    long countByIsActiveTrue();

    @Query("SELECT COUNT(v) FROM VisitorLog v WHERE v.student.id = :studentId AND v.isActive = true")
    long countActiveVisitorsByStudent(@Param("studentId") Long studentId);

    @Query("SELECT v FROM VisitorLog v WHERE v.room.id = :roomId AND v.isActive = true")
    List<VisitorLog> findActiveVisitorsByRoom(@Param("roomId") Long roomId);

    @Query("SELECT v FROM VisitorLog v WHERE v.student.id = :studentId ORDER BY v.checkInTime DESC")
    List<VisitorLog> findLatestVisitorsByStudent(@Param("studentId") Long studentId);

    @Query("SELECT v FROM VisitorLog v WHERE v.visitorName LIKE %:name% OR v.visitorPhone LIKE %:phone%")
    List<VisitorLog> searchByVisitorNameOrPhone(@Param("name") String name, @Param("phone") String phone);

    @Query("SELECT COUNT(v) FROM VisitorLog v WHERE v.purpose = :purpose")
    long countByPurpose(@Param("purpose") String purpose);

    @Query("SELECT v.purpose, COUNT(v) FROM VisitorLog v GROUP BY v.purpose")
    List<Object[]> getVisitorPurposeStats();

    @Query("SELECT v FROM VisitorLog v WHERE v.checkOutTime IS NULL AND v.isActive = true")
    List<VisitorLog> findActiveVisitors();

    @Query("SELECT v FROM VisitorLog v WHERE v.checkInTime >= :startTime AND v.checkInTime <= :endTime")
    List<VisitorLog> findVisitorsBetweenDates(@Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(v) FROM VisitorLog v WHERE v.checkInTime >= :startTime AND v.checkInTime <= :endTime")
    long countVisitorsBetweenDates(@Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    @Query("SELECT v.room.id, COUNT(v) FROM VisitorLog v GROUP BY v.room.id ORDER BY COUNT(v) DESC")
    List<Object[]> getMostVisitedRooms();
}