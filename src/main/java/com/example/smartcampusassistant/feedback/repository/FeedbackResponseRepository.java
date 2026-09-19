package com.example.smartcampusassistant.feedback.repository;

import com.example.smartcampusassistant.feedback.entity.FeedbackResponse;
import com.example.smartcampusassistant.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackResponseRepository extends JpaRepository<FeedbackResponse, Long> {

    List<FeedbackResponse> findByStudent(User student);

    List<FeedbackResponse> findByStudentId(Long studentId);

    List<FeedbackResponse> findByFeedbackId(Long feedbackId);

    boolean existsByFeedbackIdAndStudentId(Long feedbackId, Long studentId);
}