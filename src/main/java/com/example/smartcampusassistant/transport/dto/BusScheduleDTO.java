package com.example.smartcampusassistant.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusScheduleDTO {
    private Long id;
    private Long busId;
    private String busNumber;
    private String driverName;
    private String driverPhone;
    private Long routeId;
    private String routeName;
    private String dayOfWeek;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Integer availableSeats;
    private Integer totalSeats;
    private Boolean isActive;
}