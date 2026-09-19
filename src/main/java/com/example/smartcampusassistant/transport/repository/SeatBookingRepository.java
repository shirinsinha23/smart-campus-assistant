package com.example.smartcampusassistant.transport.repository;

import com.example.smartcampusassistant.transport.entity.SeatBooking;
import com.example.smartcampusassistant.transport.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatBookingRepository extends JpaRepository<SeatBooking, Long> {

    List<SeatBooking> findByUserId(Long userId);

    List<SeatBooking> findByScheduleId(Long scheduleId);

    List<SeatBooking> findByScheduleIdAndSeatNumber(Long scheduleId, String seatNumber);

    @Query("SELECT COUNT(sb) FROM SeatBooking sb WHERE sb.schedule.id = :scheduleId AND sb.seatStatus = 'BOOKED'")
    long countBookedSeatsBySchedule(@Param("scheduleId") Long scheduleId);

    @Query("SELECT sb FROM SeatBooking sb WHERE sb.schedule.id = :scheduleId AND sb.seatStatus = 'BOOKED'")
    List<SeatBooking> findBookedSeatsBySchedule(@Param("scheduleId") Long scheduleId);

    @Query("SELECT sb FROM SeatBooking sb WHERE sb.user.id = :userId AND sb.bookingStatus = 'CONFIRMED' AND sb.travelDate > :currentTime")
    List<SeatBooking> findUpcomingBookings(@Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);
}