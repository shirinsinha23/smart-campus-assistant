// PlacementStatistics.java
package com.example.smartcampusassistant.placement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "placement_statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String academicYear;

    private Integer totalStudents;

    private Integer placedStudents;

    private BigDecimal highestPackage;

    private BigDecimal averagePackage;

    private Integer totalCompanies;

    private Integer totalDrives;
}