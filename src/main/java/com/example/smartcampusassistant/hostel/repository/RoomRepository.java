package com.example.smartcampusassistant.hostel.repository;

import com.example.smartcampusassistant.hostel.entity.Room;
import com.example.smartcampusassistant.hostel.enums.RoomFacility;
import com.example.smartcampusassistant.hostel.enums.RoomStatus;
import com.example.smartcampusassistant.hostel.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHostelId(Long hostelId);
    List<Room> findByHostelIdAndStatus(Long hostelId, RoomStatus status);
    List<Room> findByHostelIdAndRoomTypeAndStatus(Long hostelId, RoomType roomType, RoomStatus status);
    List<Room> findByRoomTypeAndStatus(RoomType roomType, RoomStatus status);
    List<Room> findByStatus(RoomStatus status);
    Optional<Room> findByRoomNumber(String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.hostel.id = :hostelId AND r.status = 'AVAILABLE' AND r.availableSeats > 0")
    List<Room> findAvailableRoomsByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT r FROM Room r WHERE :facility MEMBER OF r.facilities")
    List<Room> findByFacilitiesContaining(@Param("facility") RoomFacility facility);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.hostel.id = :hostelId AND r.status = 'AVAILABLE'")
    Long countAvailableRoomsByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT r FROM Room r WHERE r.hostel.id = :hostelId AND r.roomType = :roomType AND r.status = 'AVAILABLE' AND r.availableSeats > 0")
    List<Room> findAvailableRoomsByHostelAndType(@Param("hostelId") Long hostelId, @Param("roomType") RoomType roomType);
}