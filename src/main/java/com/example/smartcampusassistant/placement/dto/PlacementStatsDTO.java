// PlacementStatsDTO.java
package com.example.smartcampusassistant.placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementStatsDTO {
    private String academicYear;
    private Integer totalStudents;
    private Integer placedStudents;
    private BigDecimal placementPercentage;
    private BigDecimal highestPackage;
    private BigDecimal averagePackage;
    private Integer totalCompanies;
    private Integer totalDrives;
    private Integer activeDrives;
}