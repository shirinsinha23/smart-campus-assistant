package com.example.smartcampusassistant.hostel.entity;

import com.example.smartcampusassistant.hostel.enums.RoomBedType;
import com.example.smartcampusassistant.hostel.enums.RoomFacility;
import com.example.smartcampusassistant.hostel.enums.RoomStatus;
import com.example.smartcampusassistant.hostel.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private String wing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    private RoomBedType bedType;

    @Column(nullable = false)
    private Integer totalRooms;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer availableSeats;

    @Builder.Default
    @Column(nullable = false)
    private Integer occupiedCount = 0;

    private Integer attachedBathrooms;

    private Integer attachedWashrooms;

    @Column(nullable = false)
    private Double rentPerMonth;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_facilities", joinColumns = @JoinColumn(name = "room_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "facility")
    private List<RoomFacility> facilities = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String additionalAmenities;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    private Double roomSize;

    @Builder.Default
    private Boolean hasBalcony = false;

    @Builder.Default
    private Boolean hasKitchen = false;

    @Builder.Default
    private Boolean hasLivingRoom = false;

    private String furnishedType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    @JsonIgnore  // 👈 ADD THIS - Prevents infinite recursion
    private Hostel hostel;

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // 👈 ADD THIS - Prevents infinite recursion
    private List<RoomAllocation> allocations = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}