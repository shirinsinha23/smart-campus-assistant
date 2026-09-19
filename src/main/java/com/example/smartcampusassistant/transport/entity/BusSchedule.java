package com.example.smartcampusassistant.transport.entity;

import com.example.smartcampusassistant.transport.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bus_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private BusRoute route;

    @Column(nullable = false)
    private String dayOfWeek;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SeatBooking> bookings = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to get available seats count
    @Transient
    public int getAvailableSeatsCount() {
        if (bookings == null || bookings.isEmpty()) {
            return bus != null ? bus.getCapacity() : 0;
        }
        long bookedCount = bookings.stream()
                .filter(b -> b.getSeatStatus() == SeatStatus.BOOKED)
                .count();
        return bus != null ? bus.getCapacity() - (int) bookedCount : 0;
    }

    // Helper method to get booked seats
    @Transient
    public List<String> getBookedSeats() {
        if (bookings == null || bookings.isEmpty()) {
            return new ArrayList<>();
        }
        return bookings.stream()
                .filter(b -> b.getSeatStatus() == SeatStatus.BOOKED)
                .map(SeatBooking::getSeatNumber)
                .collect(java.util.stream.Collectors.toList());
    }
}