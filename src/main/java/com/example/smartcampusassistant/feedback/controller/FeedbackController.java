package com.example.smartcampusassistant.feedback.controller;

import com.example.smartcampusassistant.feedback.dto.*;
import com.example.smartcampusassistant.feedback.entity.Feedback;
import com.example.smartcampusassistant.feedback.entity.FeedbackQuestion;
import com.example.smartcampusassistant.feedback.entity.FeedbackResponse;
import com.example.smartcampusassistant.feedback.enums.FeedbackCategory;
import com.example.smartcampusassistant.feedback.service.FeedbackService;
import com.example.smartcampusassistant.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    // ===== FEEDBACK ENDPOINTS =====

    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Feedback> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        Long studentId = SecurityUtils.getCurrentUserId();
        log.info("📝 Submit feedback from student {}", studentId);
        Feedback feedback = feedbackService.submitFeedback(studentId, request);
        return new ResponseEntity<>(feedback, HttpStatus.CREATED);
    }

    @PutMapping("/{feedbackId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Feedback> updateFeedback(
            @PathVariable Long feedbackId,
            @Valid @RequestBody FeedbackRequest request) {
        Long studentId = SecurityUtils.getCurrentUserId();
        Feedback existing = feedbackService.getFeedbackByIdForUpdate(feedbackId);
        if (!existing.getStudent().getId().equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Feedback updated = feedbackService.updateFeedback(feedbackId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId) {
        Long studentId = SecurityUtils.getCurrentUserId();
        Feedback existing = feedbackService.getFeedbackByIdForUpdate(feedbackId);
        if (!existing.getStudent().getId().equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{feedbackId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFeedbackByAdmin(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedbackPermanently(feedbackId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")   // ✅ correct
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    @GetMapping("/my-feedback")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<FeedbackResponseDTO>> getMyFeedback() {
        Long studentId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(feedbackService.getFeedbackByStudent(studentId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbackByCategory(
            @PathVariable FeedbackCategory category) {
        return ResponseEntity.ok(feedbackService.getFeedbackByCategory(category));
    }

    @GetMapping("/{feedbackId:\\d+}")
    public ResponseEntity<FeedbackResponseDTO> getFeedbackById(@PathVariable Long feedbackId) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(feedbackId));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackStatisticsDTO> getFeedbackStatistics() {
        return ResponseEntity.ok(feedbackService.getFeedbackStatistics());
    }

    @GetMapping("/stats/category/{category}")
    public ResponseEntity<Double> getAverageRatingByCategory(@PathVariable FeedbackCategory category) {
        return ResponseEntity.ok(feedbackService.getAverageRatingByCategory(category));
    }

    @GetMapping("/stats/faculty/{facultyId}")
    public ResponseEntity<Double> getAverageRatingByFaculty(@PathVariable Long facultyId) {
        return ResponseEntity.ok(feedbackService.getAverageRatingByFaculty(facultyId));
    }

    @GetMapping("/stats/subject/{subject}")
    public ResponseEntity<Double> getAverageRatingBySubject(@PathVariable String subject) {
        return ResponseEntity.ok(feedbackService.getAverageRatingBySubject(subject));
    }

    // ===== FEEDBACK QUESTIONS ENDPOINTS =====

    @PostMapping("/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackQuestion> createQuestion(
            @Valid @RequestBody FeedbackQuestionRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        FeedbackQuestion question = feedbackService.createQuestion(adminId, request);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackQuestion> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody FeedbackQuestionRequest request) {
        FeedbackQuestion question = feedbackService.updateQuestion(questionId, request);
        return ResponseEntity.ok(question);
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        feedbackService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/questions")
    public ResponseEntity<List<FeedbackQuestionDTO>> getAllQuestions() {
        return ResponseEntity.ok(feedbackService.getAllQuestions());
    }

    @GetMapping("/questions/category/{category}")
    public ResponseEntity<List<FeedbackQuestionDTO>> getQuestionsByCategory(
            @PathVariable FeedbackCategory category) {
        return ResponseEntity.ok(feedbackService.getQuestionsByCategory(category));
    }

    // ===== FEEDBACK RESPONSES ENDPOINTS =====

    @PostMapping("/{feedbackId}/responses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<FeedbackResponse> submitResponse(
            @PathVariable Long feedbackId,
            @RequestParam Long questionId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String textResponse) {
        Long studentId = SecurityUtils.getCurrentUserId();
        FeedbackResponse response = feedbackService.submitFeedbackResponse(
                feedbackId, studentId, questionId, rating, textResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{feedbackId}/responses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackResponse>> getResponsesByFeedback(
            @PathVariable Long feedbackId) {
        return ResponseEntity.ok(feedbackService.getResponsesByFeedback(feedbackId));
    }
}