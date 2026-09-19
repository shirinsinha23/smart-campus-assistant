package com.example.smartcampusassistant.hostel.repository;

import com.example.smartcampusassistant.hostel.entity.RoomAllocation;
import com.example.smartcampusassistant.hostel.enums.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAllocationRepository extends JpaRepository<RoomAllocation, Long> {

    List<RoomAllocation> findByStudentId(Long studentId);

    // REMOVED THE DUPLICATE - keep only one version
    // List<RoomAllocation> findByStudentIdAndStatus(Long studentId, AllocationStatus status);

    List<RoomAllocation> findByRoomId(Long roomId);

    List<RoomAllocation> findByStatus(AllocationStatus status);

    // This one already exists, remove the duplicate above
    Optional<RoomAllocation> findByStudentIdAndStatus(Long studentId, AllocationStatus status);

    @Query("SELECT ra FROM RoomAllocation ra WHERE ra.student.id = :studentId AND ra.status = 'ACTIVE'")
    Optional<RoomAllocation> findActiveAllocationByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(ra) FROM RoomAllocation ra WHERE ra.room.id = :roomId AND ra.status = 'ACTIVE'")
    Long countActiveAllocationsByRoom(@Param("roomId") Long roomId);

    @Query("SELECT ra FROM RoomAllocation ra WHERE ra.room.id = :roomId AND ra.status = 'ACTIVE'")
    List<RoomAllocation> findActiveAllocationsByRoom(@Param("roomId") Long roomId);

    @Query("SELECT ra FROM RoomAllocation ra WHERE ra.student.id = :studentId AND ra.status IN ('ACTIVE', 'TRANSFERRED')")
    List<RoomAllocation> findActiveOrTransferredAllocationsByStudent(@Param("studentId") Long studentId);

    @Query("SELECT ra FROM RoomAllocation ra WHERE ra.allocatedDate = :date")
    List<RoomAllocation> findByAllocatedDate(@Param("date") LocalDate date);

    @Query("SELECT ra FROM RoomAllocation ra WHERE ra.checkOutDate IS NOT NULL AND ra.checkOutDate <= :date")
    List<RoomAllocation> findCheckedOutByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(ra) FROM RoomAllocation ra WHERE ra.status = 'ACTIVE'")
    long countActiveAllocations();

    @Query("SELECT COUNT(ra) FROM RoomAllocation ra WHERE ra.status = 'CHECKED_OUT'")
    long countCheckedOutAllocations();

    @Query("SELECT COUNT(ra) FROM RoomAllocation ra WHERE ra.status = 'TRANSFERRED'")
    long countTransferredAllocations();

    @Query("SELECT COUNT(ra) FROM RoomAllocation ra WHERE ra.status = 'CANCELLED'")
    long countCancelledAllocations();

    @Query("SELECT ra.room.hostel.id, COUNT(ra) FROM RoomAllocation ra WHERE ra.status = 'ACTIVE' GROUP BY ra.room.hostel.id")
    List<Object[]> countActiveAllocationsByHostel();

    @Query("SELECT ra.room.roomType, COUNT(ra) FROM RoomAllocation ra WHERE ra.status = 'ACTIVE' GROUP BY ra.room.roomType")
    List<Object[]> countActiveAllocationsByRoomType();

    @Query("SELECT ra.room.id, COUNT(ra) FROM RoomAllocation ra WHERE ra.status = 'ACTIVE' GROUP BY ra.room.id ORDER BY COUNT(ra) DESC")
    List<Object[]> getMostOccupiedRooms();

    @Query("SELECT ra FROM RoomAllocation ra WHERE ra.student.id = :studentId AND ra.status = 'ACTIVE' AND ra.room.id = :roomId")
    Optional<RoomAllocation> findActiveAllocationByStudentAndRoom(@Param("studentId") Long studentId, @Param("roomId") Long roomId);

    @Query("SELECT ra FROM RoomAllocation ra WHERE ra.allocatedBy.id = :adminId")
    List<RoomAllocation> findAllocationsByAdmin(@Param("adminId") Long adminId);

    @Query("SELECT COUNT(ra) FROM RoomAllocation ra WHERE ra.allocatedDate BETWEEN :startDate AND :endDate")
    long countAllocationsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}