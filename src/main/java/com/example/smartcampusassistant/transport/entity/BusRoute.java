package com.example.smartcampusassistant.transport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "bus_routes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_name", nullable = false, length = 100)
    private String routeName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_location", nullable = false, length = 100)
    private String startLocation;

    @Column(name = "end_location", nullable = false, length = 100)
    private String endLocation;

    @Column(name = "route_type", length = 20)
    @Builder.Default
    private String routeType = "CITY";

    // Store stops as comma-separated string in DB
    @Column(columnDefinition = "TEXT")
    private String stops;

    @Column(name = "total_distance_km")
    @Builder.Default
    private Double totalDistanceKm = 0.0;

    @Column(name = "estimated_duration_minutes")
    @Builder.Default
    private Integer estimatedDurationMinutes = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to get stops as list
    @Transient
    public List<String> getStopsList() {
        if (stops == null || stops.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(stops.split(","));
    }

    // Helper method to set stops from list
    public void setStopsList(List<String> stopsList) {
        if (stopsList == null || stopsList.isEmpty()) {
            this.stops = "";
        } else {
            this.stops = String.join(",", stopsList);
        }
    }
}