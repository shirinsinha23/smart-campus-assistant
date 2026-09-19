package com.example.smartcampusassistant.placement.repository;

import com.example.smartcampusassistant.placement.entity.DriveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriveApplicationRepository extends JpaRepository<DriveApplication, Long> {

    List<DriveApplication> findByStudentId(Long studentId);

    List<DriveApplication> findByDriveId(Long driveId);

    List<DriveApplication> findByStatus(String status);

    Optional<DriveApplication> findByDriveIdAndStudentId(Long driveId, Long studentId);

    @Query("SELECT da FROM DriveApplication da WHERE da.drive.id = :driveId AND da.status = 'SHORTLISTED'")
    List<DriveApplication> findShortlistedApplications(@Param("driveId") Long driveId);

    @Query("SELECT COUNT(da) FROM DriveApplication da WHERE da.drive.id = :driveId")
    long countByDriveId(@Param("driveId") Long driveId);

    @Query("SELECT da FROM DriveApplication da WHERE da.student.id = :studentId AND da.status IN ('PENDING', 'SHORTLISTED')")
    List<DriveApplication> findActiveApplicationsByStudent(@Param("studentId") Long studentId);

    // 👇 ADD THESE MISSING METHODS
    @Query("SELECT COUNT(DISTINCT da.student.id) FROM DriveApplication da")
    long countDistinctStudentIds();

    @Query("SELECT COUNT(da) FROM DriveApplication da WHERE da.appliedAt >= :daysAgo")
    long countRecentApplications(@Param("daysAgo") LocalDateTime daysAgo);

    @Query("SELECT COUNT(da) FROM DriveApplication da WHERE da.status = 'SELECTED' AND da.updatedAt >= :startDate")
    long countSelectedThisMonth(@Param("startDate") LocalDateTime startDate);
}