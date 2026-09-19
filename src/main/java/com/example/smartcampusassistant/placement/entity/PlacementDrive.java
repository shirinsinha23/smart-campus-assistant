// PlacementDrive.java
package com.example.smartcampusassistant.placement.entity;

import com.example.smartcampusassistant.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "placement_drives")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String driveType; // ON_CAMPUS, OFF_CAMPUS, VIRTUAL

    private String mode; // OFFLINE, ONLINE, HYBRID

    private String jobRole;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    private BigDecimal salaryPackage;

    private BigDecimal minCgpa;

    private Integer maxBacklogs;

    @Column(columnDefinition = "TEXT")
    private String eligibleBranches;

    @Column(columnDefinition = "TEXT")
    private String eligibleYears;

    private LocalDate applicationStartDate;

    private LocalDate applicationEndDate;

    private LocalDate driveDate;

    private LocalTime driveTime;

    private String venue;

    private Integer maxApplicants;

    @Builder.Default
    private String status = "UPCOMING"; // UPCOMING, ONGOING, COMPLETED, CANCELLED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "drive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DriveApplication> applications = new ArrayList<>();
}