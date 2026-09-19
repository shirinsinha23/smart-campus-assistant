package com.example.smartcampusassistant.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByFacultyIdOrderByCreatedAtDesc(Long facultyId);
    List<Assignment> findAllByOrderByDeadlineAsc();
}