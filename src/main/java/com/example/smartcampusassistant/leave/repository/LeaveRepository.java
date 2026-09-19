package com.example.smartcampusassistant.leave.repository;

import com.example.smartcampusassistant.leave.entity.Leave;
import com.example.smartcampusassistant.leave.enums.LeaveStatus;
import com.example.smartcampusassistant.leave.enums.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // Basic queries
    Page<Leave> findByUserId(Long userId, Pageable pageable);
    List<Leave> findByUserId(Long userId);

    Page<Leave> findByStatus(LeaveStatus status, Pageable pageable);
    List<Leave> findByStatus(LeaveStatus status);

    // Combined query (only one definition)
    @Query("SELECT l FROM Leave l WHERE l.user.id = :userId AND l.status = :status")
    Page<Leave> findByUserIdAndStatus(@Param("userId") Long userId,
                                      @Param("status") LeaveStatus status,
                                      Pageable pageable);

    @Query("SELECT l FROM Leave l WHERE l.user.id = :userId AND l.leaveType = :leaveType")
    Page<Leave> findByUserIdAndLeaveType(@Param("userId") Long userId,
                                         @Param("leaveType") LeaveType leaveType,
                                         Pageable pageable);

    @Query("SELECT l FROM Leave l WHERE l.status = :status AND l.leaveType = :leaveType")
    Page<Leave> findByStatusAndLeaveType(@Param("status") LeaveStatus status,
                                         @Param("leaveType") LeaveType leaveType,
                                         Pageable pageable);

    @Query("SELECT l FROM Leave l WHERE l.user.id = :userId AND l.status = :status AND l.leaveType = :leaveType")
    Page<Leave> findByUserIdAndStatusAndLeaveType(@Param("userId") Long userId,
                                                  @Param("status") LeaveStatus status,
                                                  @Param("leaveType") LeaveType leaveType,
                                                  Pageable pageable);

    Page<Leave> findByLeaveType(LeaveType leaveType, Pageable pageable);

    // Date range queries
    @Query("SELECT l FROM Leave l WHERE l.user.id = :userId AND l.startDate <= :endDate AND l.endDate >= :startDate")
    List<Leave> findOverlappingLeaves(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM Leave l WHERE l.user.id = :userId AND l.startDate >= :fromDate AND l.endDate <= :toDate")
    Page<Leave> findByUserIdAndDateRange(@Param("userId") Long userId,
                                         @Param("fromDate") LocalDate fromDate,
                                         @Param("toDate") LocalDate toDate,
                                         Pageable pageable);

    @Query("SELECT l FROM Leave l WHERE l.startDate >= :startDate AND l.endDate <= :endDate")
    List<Leave> findByDateRange(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM Leave l WHERE l.startDate >= :startDate AND l.endDate <= :endDate AND l.status = :status")
    List<Leave> findByDateRangeAndStatus(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("status") LeaveStatus status);

    @Query("SELECT l FROM Leave l WHERE l.startDate <= :date AND l.endDate >= :date AND l.status = 'APPROVED'")
    List<Leave> findApprovedLeavesOnDate(@Param("date") LocalDate date);

    @Query("SELECT l FROM Leave l WHERE l.user.id = :userId AND l.startDate <= :date AND l.endDate >= :date AND l.status = 'APPROVED'")
    List<Leave> findApprovedLeavesByUserOnDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT l FROM Leave l WHERE l.startDate >= :startDate AND l.endDate <= :endDate AND l.status = 'APPROVED'")
    List<Leave> findApprovedLeavesInDateRange(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // Search query
    @Query("SELECT l FROM Leave l WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(l.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(l.user.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(l.user.loginId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Leave> searchLeaves(@Param("search") String search, Pageable pageable);

    // Stats queries
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.user.id = :userId AND l.status = 'PENDING'")
    long countPendingLeavesByUser(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(l.totalDays), 0) FROM Leave l WHERE l.user.id = :userId " +
            "AND l.status = 'APPROVED' AND YEAR(l.startDate) = YEAR(CURRENT_DATE)")
    int sumApprovedLeaveDaysThisYear(@Param("userId") Long userId);

    @Query("SELECT l FROM Leave l WHERE l.approvedBy.id = :facultyId OR l.user.id = :facultyId")
    List<Leave> findLeavesByFaculty(@Param("facultyId") Long facultyId);
}