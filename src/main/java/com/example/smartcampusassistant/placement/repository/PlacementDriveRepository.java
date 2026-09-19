package com.example.smartcampusassistant.placement.repository;

import com.example.smartcampusassistant.placement.entity.PlacementDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {

    List<PlacementDrive> findByStatus(String status);

    List<PlacementDrive> findByCompanyId(Long companyId);

    @Query("SELECT pd FROM PlacementDrive pd WHERE pd.status = 'UPCOMING' ORDER BY pd.driveDate ASC")
    List<PlacementDrive> findUpcomingDrives();

    @Query("SELECT pd FROM PlacementDrive pd WHERE pd.status = 'ONGOING' ORDER BY pd.driveDate ASC")
    List<PlacementDrive> findOngoingDrives();

    @Query("SELECT pd FROM PlacementDrive pd WHERE pd.applicationStartDate <= :date AND pd.applicationEndDate >= :date")
    List<PlacementDrive> findActiveApplications(@Param("date") LocalDate date);

    @Query("SELECT COUNT(pd) FROM PlacementDrive pd WHERE pd.status IN ('UPCOMING', 'ONGOING')")
    long countActiveDrives();

    // 👇 ADD THESE MISSING METHODS
    @Query("SELECT COUNT(pd) FROM PlacementDrive pd WHERE pd.createdAt >= :daysAgo")
    long countNewDrives(@Param("daysAgo") LocalDateTime daysAgo);

    @Query("SELECT pd.status, COUNT(pd) FROM PlacementDrive pd GROUP BY pd.status")
    List<Object[]> countDrivesByStatus();
}