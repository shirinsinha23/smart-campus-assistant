package com.example.smartcampusassistant.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatBookingDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userLoginId;
    private Long scheduleId;
    private String busNumber;
    private String routeName;
    private String seatNumber;
    private String seatStatus;
    private String seatStatusDisplay;
    private String bookingStatus;
    private String bookingStatusDisplay;
    private LocalDate bookingDate;
    private LocalDateTime travelDate;
    private String cancellationReason;
}