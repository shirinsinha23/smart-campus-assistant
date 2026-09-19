package com.example.smartcampusassistant.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusTrackingDTO {
    private Long busId;
    private String busNumber;
    private String driverName;
    private String driverPhone;
    private Double latitude;
    private Double longitude;
    private String locationName;
    private Double speedKmh;
    private Double headingDegrees;
    private LocalDateTime timestamp;
    private Boolean isActive;
    private Integer availableSeats;
    private Integer totalSeats;
}