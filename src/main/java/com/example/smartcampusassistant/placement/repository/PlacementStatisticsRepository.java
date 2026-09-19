package com.example.smartcampusassistant.placement.repository;

import com.example.smartcampusassistant.placement.entity.PlacementStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementStatisticsRepository extends JpaRepository<PlacementStatistics, Long> {

    Optional<PlacementStatistics> findByAcademicYear(String academicYear);

    Optional<PlacementStatistics> findFirstByOrderByAcademicYearDesc();

    List<PlacementStatistics> findAllByOrderByAcademicYearDesc();

    @Query("SELECT ps FROM PlacementStatistics ps WHERE ps.academicYear LIKE %:year%")
    List<PlacementStatistics> findByAcademicYearContaining(@Param("year") String year);

    @Query("SELECT MAX(ps.academicYear) FROM PlacementStatistics ps")
    String findLatestAcademicYear();

    @Query("SELECT ps.academicYear, ps.placedStudents FROM PlacementStatistics ps ORDER BY ps.academicYear DESC")
    List<Object[]> getPlacementTrend();
}