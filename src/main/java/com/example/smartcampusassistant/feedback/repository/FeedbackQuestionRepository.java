package com.example.smartcampusassistant.feedback.repository;

import com.example.smartcampusassistant.feedback.entity.FeedbackQuestion;
import com.example.smartcampusassistant.feedback.enums.FeedbackCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackQuestionRepository extends JpaRepository<FeedbackQuestion, Long> {

    List<FeedbackQuestion> findByCategory(FeedbackCategory category);

    List<FeedbackQuestion> findByCategoryAndIsActiveTrue(FeedbackCategory category);

    List<FeedbackQuestion> findByIsActiveTrue();

    List<FeedbackQuestion> findByCategoryAndIsRequiredTrue(FeedbackCategory category);
}