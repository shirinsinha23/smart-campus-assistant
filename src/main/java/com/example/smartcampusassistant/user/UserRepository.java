package com.example.smartcampusassistant.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ Email and Login ID lookups
    Optional<User> findByEmail(String email);

    Optional<User> findByLoginId(String loginId);

    boolean existsByEmail(String email);

    boolean existsByLoginId(String loginId);

    List<User> findByRole(Role role);

    long countByRole(Role role);

    List<User> findTop5ByOrderByCreatedAtDesc();

    // ✅ Additional methods for Placement module
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findByRoleAndIsActiveTrue(@Param("role") Role role);

    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.isActive = true ORDER BY u.name ASC")
    List<User> findAllActiveStudentsOrderedByName();

    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.isActive = true AND LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchStudents(@Param("keyword") String keyword);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'STUDENT' AND u.isActive = true")
    long countActiveStudents();

    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.isActive = true")
    List<User> findAllActiveStudents();

    // ✅ Get students by stream
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.stream = :stream")
    List<User> findStudentsByStream(@Param("stream") String stream);

    // ✅ FIXED: Get faculty by subject using the correct field name
    // The TimetableSlot entity has 'faculty' (User type), not 'facultyId'
    @Query("SELECT DISTINCT u FROM User u JOIN TimetableSlot ts ON u.id = ts.faculty.id WHERE u.role = 'FACULTY' AND ts.subject = :subject")
    List<User> findFacultyBySubject(@Param("subject") String subject);
}