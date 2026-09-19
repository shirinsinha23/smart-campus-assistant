package com.example.smartcampusassistant.user.repository;

import com.example.smartcampusassistant.user.entity.FacultySubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacultySubjectRepository extends JpaRepository<FacultySubject, Long> {

    List<FacultySubject> findByFacultyId(Long facultyId);

    boolean existsByFacultyIdAndSubject(Long facultyId, String subject);

    void deleteByFacultyId(Long facultyId);
}