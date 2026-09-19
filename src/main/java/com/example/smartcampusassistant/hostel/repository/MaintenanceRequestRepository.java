package com.example.smartcampusassistant.hostel.repository;

import com.example.smartcampusassistant.hostel.entity.MaintenanceRequest;
import com.example.smartcampusassistant.hostel.enums.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByReportedById(Long studentId);

    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);

    List<MaintenanceRequest> findByRoomId(Long roomId);

    List<MaintenanceRequest> findByReportedByIdAndStatus(Long studentId, MaintenanceStatus status);

    List<MaintenanceRequest> findByCategory(String category);

    List<MaintenanceRequest> findByPriority(String priority);

    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.status = 'PENDING' ORDER BY mr.createdAt ASC")
    List<MaintenanceRequest> findPendingRequestsOrderedByCreatedAt();

    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.assignedTo.id = :staffId")
    List<MaintenanceRequest> findByAssignedTo(@Param("staffId") Long staffId);

    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.assignedTo.id = :staffId AND mr.status = :status")
    List<MaintenanceRequest> findByAssignedToAndStatus(@Param("staffId") Long staffId, @Param("status") MaintenanceStatus status);

    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr WHERE mr.status = 'PENDING'")
    long countPendingRequests();

    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr WHERE mr.status = 'IN_PROGRESS'")
    long countInProgressRequests();

    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr WHERE mr.status = 'RESOLVED'")
    long countResolvedRequests();

    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr WHERE mr.status = 'REJECTED'")
    long countRejectedRequests();

    @Query("SELECT mr.category, COUNT(mr) FROM MaintenanceRequest mr GROUP BY mr.category")
    List<Object[]> countRequestsByCategory();

    @Query("SELECT mr.priority, COUNT(mr) FROM MaintenanceRequest mr GROUP BY mr.priority")
    List<Object[]> countRequestsByPriority();

    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.createdAt BETWEEN :startDate AND :endDate")
    List<MaintenanceRequest> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(DATEDIFF(mr.resolvedAt, mr.createdAt)) FROM MaintenanceRequest mr WHERE mr.status = 'RESOLVED'")
    Double getAverageResolutionTimeInDays();

    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.status = 'PENDING' AND mr.priority = 'HIGH'")
    List<MaintenanceRequest> findHighPriorityPendingRequests();

    @Query("SELECT mr.room.id, COUNT(mr) FROM MaintenanceRequest mr WHERE mr.status != 'RESOLVED' GROUP BY mr.room.id ORDER BY COUNT(mr) DESC")
    List<Object[]> getRoomsWithMostMaintenanceRequests();

    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.room.hostel.id = :hostelId")
    List<MaintenanceRequest> findByHostelId(@Param("hostelId") Long hostelId);

    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.room.hostel.id = :hostelId AND mr.status = :status")
    List<MaintenanceRequest> findByHostelIdAndStatus(@Param("hostelId") Long hostelId, @Param("status") MaintenanceStatus status);
}