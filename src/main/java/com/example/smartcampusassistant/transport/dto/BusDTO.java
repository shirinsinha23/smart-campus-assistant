package com.example.smartcampusassistant.transport.dto;

import com.example.smartcampusassistant.transport.enums.BusStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusDTO {
    private Long id;
    private String busNumber;
    private String driverName;
    private String driverPhone;
    private Integer capacity;
    private String registrationNumber;
    private String status;
    private String statusDisplay;
    private String lastKnownLocation;
    private LocalDateTime lastUpdated;
    private Integer availableSeats;
    private LocalDateTime createdAt;
}