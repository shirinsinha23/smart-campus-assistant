package com.example.smartcampusassistant.placement.repository;

import com.example.smartcampusassistant.placement.entity.StudentResume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentResumeRepository extends JpaRepository<StudentResume, Long> {

    List<StudentResume> findByStudentId(Long studentId);

    Optional<StudentResume> findByStudentIdAndIsPrimaryTrue(Long studentId);

    @Query("SELECT sr FROM StudentResume sr WHERE sr.student.id = :studentId ORDER BY sr.uploadedAt DESC")
    List<StudentResume> findLatestByStudentId(@Param("studentId") Long studentId);

    @Modifying
    @Transactional
    @Query("UPDATE StudentResume sr SET sr.isPrimary = false WHERE sr.student.id = :studentId")
    void unsetPrimaryResume(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(sr) FROM StudentResume sr WHERE sr.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);
}