// PlacementDriveDTO.java
package com.example.smartcampusassistant.placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementDriveDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLogo;
    private String title;
    private String description;
    private String driveType;
    private String driveTypeDisplay;
    private String mode;
    private String modeDisplay;
    private String jobRole;
    private String jobDescription;
    private BigDecimal salaryPackage;
    private String salaryDisplay;
    private BigDecimal minCgpa;
    private Integer maxBacklogs;
    private String eligibleBranches;
    private String eligibleYears;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private LocalDate driveDate;
    private LocalTime driveTime;
    private String venue;
    private Integer maxApplicants;
    private Integer totalApplications;
    private String status;
    private String statusDisplay;
    private String statusColor;
    private Boolean isApplied;
}