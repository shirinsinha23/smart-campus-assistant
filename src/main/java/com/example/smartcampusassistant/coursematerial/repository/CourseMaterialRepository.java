package com.example.smartcampusassistant.coursematerial.repository;

import com.example.smartcampusassistant.coursematerial.entity.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {

    List<CourseMaterial> findBySubject(String subject);

    List<CourseMaterial> findByType(String type);

    List<CourseMaterial> findByUploadedById(Long userId);

    @Query("SELECT DISTINCT c.subject FROM CourseMaterial c")
    List<String> findDistinctSubjects();

    @Query("SELECT c FROM CourseMaterial c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CourseMaterial> searchByKeyword(@Param("keyword") String keyword);

    // ✅ NEW: Get materials by multiple subjects
    @Query("SELECT c FROM CourseMaterial c WHERE c.subject IN :subjects")
    List<CourseMaterial> findBySubjects(@Param("subjects") List<String> subjects);
}