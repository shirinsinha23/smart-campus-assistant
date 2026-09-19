package com.example.smartcampusassistant.feedback.repository;

import com.example.smartcampusassistant.feedback.entity.Feedback;
import com.example.smartcampusassistant.feedback.enums.FeedbackCategory;
import com.example.smartcampusassistant.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByStudent(User student);

    List<Feedback> findByStudentId(Long studentId);

    List<Feedback> findByCategory(FeedbackCategory category);

    List<Feedback> findByCategoryAndIsActiveTrue(FeedbackCategory category);

    @Query("SELECT f FROM Feedback f WHERE f.student.id = :studentId AND f.isActive = true")
    List<Feedback> findActiveByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.category = :category AND f.isActive = true")
    Double getAverageRatingByCategory(@Param("category") FeedbackCategory category);

    @Query("SELECT f.category, AVG(f.rating) FROM Feedback f WHERE f.isActive = true GROUP BY f.category")
    List<Object[]> getAverageRatingByAllCategories();

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.student.id = :studentId AND f.isActive = true")
    Long countByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.facultyId = :facultyId AND f.isActive = true")
    Double getAverageRatingByFacultyId(@Param("facultyId") Long facultyId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.subject = :subject AND f.isActive = true")
    Double getAverageRatingBySubject(@Param("subject") String subject);
}