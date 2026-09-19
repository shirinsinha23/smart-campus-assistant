package com.example.smartcampusassistant.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusRouteDTO {
    private Long id;
    private String routeName;
    private String description;
    private String startLocation;
    private String endLocation;
    private String routeType;
    private List<String> stops;
    private Double totalDistanceKm;
    private Integer estimatedDurationMinutes;
    private Boolean isActive;
}