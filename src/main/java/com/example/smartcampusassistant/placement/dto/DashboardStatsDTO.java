package com.example.smartcampusassistant.placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // ===== COMPANY STATS =====
    private Integer totalCompanies;
    private Integer activeCompanies;

    // ===== DRIVE STATS =====
    private Integer totalDrives;
    private Integer activeDrives;
    private Integer upcomingDrives;
    private Integer ongoingDrives;
    private Integer completedDrives;
    private Integer cancelledDrives;

    // ===== APPLICATION STATS =====
    private Integer totalApplications;
    private Integer pendingApplications;
    private Integer shortlisted;
    private Integer selected;
    private Integer rejected;
    private Integer waiting;

    // ===== INTERVIEW STATS =====
    private Integer totalInterviews;
    private Integer scheduledInterviews;
    private Integer completedInterviews;
    private Integer cancelledInterviews;

    // ===== STUDENT STATS =====
    private Integer totalStudentsEligible;
    private Integer studentsApplied;
    private Integer studentsPlaced;
    private Integer studentsShortlisted;

    // ===== PLACEMENT METRICS =====
    private Double placementPercentage;
    private Double highestPackage;
    private Double averagePackage;
    private Double medianPackage;

    // ===== RECENT ACTIVITY =====
    private Integer recentApplications;  // Last 7 days
    private Integer newDrives;           // Last 30 days
    private Integer interviewsThisWeek;
    private Integer offersThisMonth;

    // ===== TRENDS =====
    private Double applicationsGrowth;   // Month over month
    private Double placementGrowth;      // Year over year
    private Double averageSalaryGrowth;  // Year over year

    // ===== BRANCH WISE STATS =====
    // Could be a Map<String, Integer> or List<BranchStatsDTO>
    private Object branchWiseApplications;
    private Object branchWisePlacements;

    // ===== COMPANY WISE STATS =====
    private Object topCompaniesByHires;
    private Object topCompaniesByPackage;

    // ===== CHART DATA =====
    private Object monthlyApplications;
    private Object monthlyPlacements;
    private Object drivesByType;

    // ===== CONSTRUCTOR FOR BASIC STATS =====
    public static DashboardStatsDTOBuilder builder() {
        return new DashboardStatsDTOBuilder();
    }

    // ===== NESTED CLASS FOR BRANCH STATS =====
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchStatsDTO {
        private String branch;
        private Integer totalStudents;
        private Integer applied;
        private Integer shortlisted;
        private Integer placed;
        private Double placementPercentage;
    }

    // ===== NESTED CLASS FOR COMPANY STATS =====
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyHireStatsDTO {
        private Long companyId;
        private String companyName;
        private String companyLogo;
        private Integer totalHired;
        private Double averagePackage;
        private Double highestPackage;
    }

    // ===== NESTED CLASS FOR MONTHLY DATA =====
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyDataDTO {
        private String month;
        private Integer year;
        private Integer applications;
        private Integer placements;
        private Integer drives;
    }
}