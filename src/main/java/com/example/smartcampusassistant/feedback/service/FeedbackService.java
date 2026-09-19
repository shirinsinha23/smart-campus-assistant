package com.example.smartcampusassistant.feedback.service;

import com.example.smartcampusassistant.exception.ResourceNotFoundException;
import com.example.smartcampusassistant.feedback.dto.*;
import com.example.smartcampusassistant.feedback.entity.Feedback;
import com.example.smartcampusassistant.feedback.entity.FeedbackQuestion;
import com.example.smartcampusassistant.feedback.entity.FeedbackResponse;
import com.example.smartcampusassistant.feedback.enums.FeedbackCategory;
import com.example.smartcampusassistant.feedback.repository.FeedbackQuestionRepository;
import com.example.smartcampusassistant.feedback.repository.FeedbackRepository;
import com.example.smartcampusassistant.feedback.repository.FeedbackResponseRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackQuestionRepository feedbackQuestionRepository;
    private final FeedbackResponseRepository feedbackResponseRepository;
    private final UserRepository userRepository;

    // ===== FEEDBACK CRUD =====

    @Transactional
    public Feedback submitFeedback(Long studentId, FeedbackRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Feedback feedback = Feedback.builder()
                .student(student)
                .title(request.getTitle())
                .comment(request.getComment())
                .rating(request.getRating())
                .category(request.getCategory())
                .subject(request.getSubject())
                .facultyId(request.getFacultyId())
                .courseName(request.getCourseName())
                .isAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false)
                .isActive(true)
                .build();

        return feedbackRepository.save(feedback);
    }

    @Transactional
    public Feedback updateFeedback(Long feedbackId, FeedbackRequest request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

        feedback.setTitle(request.getTitle());
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        feedback.setCategory(request.getCategory());
        feedback.setSubject(request.getSubject());
        feedback.setFacultyId(request.getFacultyId());
        feedback.setCourseName(request.getCourseName());
        feedback.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : feedback.getIsAnonymous());

        return feedbackRepository.save(feedback);
    }

    @Transactional
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

        feedback.setIsActive(false);
        feedbackRepository.save(feedback);
    }

    @Transactional
    public void deleteFeedbackPermanently(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

        List<FeedbackResponse> responses = feedbackResponseRepository.findByFeedbackId(feedbackId);
        feedbackResponseRepository.deleteAll(responses);
        feedbackRepository.delete(feedback);
    }

    // ===== FEEDBACK QUERIES =====

    public List<FeedbackResponseDTO> getAllFeedback() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeedbackResponseDTO> getFeedbackByStudent(Long studentId) {
        List<Feedback> feedbacks = feedbackRepository.findByStudentId(studentId);
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeedbackResponseDTO> getFeedbackByCategory(FeedbackCategory category) {
        List<Feedback> feedbacks = feedbackRepository.findByCategoryAndIsActiveTrue(category);
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FeedbackResponseDTO getFeedbackById(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));
        return convertToDTO(feedback);
    }

    public Double getAverageRatingByCategory(FeedbackCategory category) {
        Double avg = feedbackRepository.getAverageRatingByCategory(category);
        return avg != null ? avg : 0.0;
    }

    public Double getAverageRatingByFaculty(Long facultyId) {
        Double avg = feedbackRepository.getAverageRatingByFacultyId(facultyId);
        return avg != null ? avg : 0.0;
    }

    public Double getAverageRatingBySubject(String subject) {
        Double avg = feedbackRepository.getAverageRatingBySubject(subject);
        return avg != null ? avg : 0.0;
    }

    public FeedbackStatisticsDTO getFeedbackStatistics() {
        List<Feedback> allFeedbacks = feedbackRepository.findAll();

        Map<String, Double> avgRatings = new HashMap<>();
        Map<String, Long> countByCategory = new HashMap<>();

        for (FeedbackCategory category : FeedbackCategory.values()) {
            Double avg = feedbackRepository.getAverageRatingByCategory(category);
            avgRatings.put(category.getDisplayName(), avg != null ? avg : 0.0);

            Long count = allFeedbacks.stream()
                    .filter(f -> f.getCategory() == category && f.getIsActive())
                    .count();
            countByCategory.put(category.getDisplayName(), count);
        }

        Double overallAvg = allFeedbacks.stream()
                .filter(Feedback::getIsActive)
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        Long uniqueStudents = allFeedbacks.stream()
                .filter(Feedback::getIsActive)
                .map(f -> f.getStudent().getId())
                .distinct()
                .count();

        return FeedbackStatisticsDTO.builder()
                .totalFeedbacks((long) allFeedbacks.stream().filter(Feedback::getIsActive).count())
                .averageRatingsByCategory(avgRatings)
                .feedbackCountByCategory(countByCategory)
                .overallAverageRating(overallAvg)
                .totalStudentsWhoGaveFeedback(uniqueStudents)
                .build();
    }

    // ===== FEEDBACK QUESTIONS =====

    @Transactional
    public FeedbackQuestion createQuestion(Long adminId, FeedbackQuestionRequest request) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + adminId));

        FeedbackQuestion question = FeedbackQuestion.builder()
                .questionText(request.getQuestionText())
                .category(request.getCategory())
                .isRequired(request.getIsRequired() != null ? request.getIsRequired() : true)
                .description(request.getDescription())
                .createdBy(admin)
                .isActive(true)
                .build();

        return feedbackQuestionRepository.save(question);
    }

    @Transactional
    public FeedbackQuestion updateQuestion(Long questionId, FeedbackQuestionRequest request) {
        FeedbackQuestion question = feedbackQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        question.setQuestionText(request.getQuestionText());
        question.setCategory(request.getCategory());
        question.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : question.getIsRequired());
        question.setDescription(request.getDescription());

        return feedbackQuestionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        FeedbackQuestion question = feedbackQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        question.setIsActive(false);
        feedbackQuestionRepository.save(question);
    }

    public List<FeedbackQuestionDTO> getAllQuestions() {
        List<FeedbackQuestion> questions = feedbackQuestionRepository.findByIsActiveTrue();
        return questions.stream()
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());
    }

    public List<FeedbackQuestionDTO> getQuestionsByCategory(FeedbackCategory category) {
        List<FeedbackQuestion> questions = feedbackQuestionRepository.findByCategoryAndIsActiveTrue(category);
        return questions.stream()
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());
    }

    // ===== FEEDBACK RESPONSES =====

    @Transactional
    public FeedbackResponse submitFeedbackResponse(Long feedbackId, Long studentId, Long questionId, Integer rating, String textResponse) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        FeedbackQuestion question = feedbackQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        if (feedbackResponseRepository.existsByFeedbackIdAndStudentId(feedbackId, studentId)) {
            throw new IllegalStateException("Student has already responded to this feedback");
        }

        FeedbackResponse response = FeedbackResponse.builder()
                .feedback(feedback)
                .question(question)
                .student(student)
                .rating(rating)
                .textResponse(textResponse)
                .build();

        return feedbackResponseRepository.save(response);
    }

    public List<FeedbackResponse> getResponsesByFeedback(Long feedbackId) {
        return feedbackResponseRepository.findByFeedbackId(feedbackId);
    }

    public List<FeedbackResponse> getResponsesByStudent(Long studentId) {
        return feedbackResponseRepository.findByStudentId(studentId);
    }

    // ===== HELPER METHODS =====

    private FeedbackResponseDTO convertToDTO(Feedback feedback) {
        Double avgRating = feedbackRepository.getAverageRatingByCategory(feedback.getCategory());

        return FeedbackResponseDTO.builder()
                .id(feedback.getId())
                .studentName(feedback.getIsAnonymous() ? "Anonymous" : feedback.getStudent().getName())
                .studentLoginId(feedback.getIsAnonymous() ? "Anonymous" : feedback.getStudent().getLoginId())
                .title(feedback.getTitle())
                .comment(feedback.getComment())
                .rating(feedback.getRating())
                .category(feedback.getCategory())
                .subject(feedback.getSubject())
                .facultyId(feedback.getFacultyId())
                .courseName(feedback.getCourseName())
                .isAnonymous(feedback.getIsAnonymous())
                .createdAt(feedback.getCreatedAt())
                .averageRatingForCategory(avgRating != null ? avgRating : 0.0)
                .build();
    }

    private FeedbackQuestionDTO convertToQuestionDTO(FeedbackQuestion question) {
        return FeedbackQuestionDTO.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .category(question.getCategory())
                .isRequired(question.getIsRequired())
                .description(question.getDescription())
                .createdBy(question.getCreatedBy() != null ? question.getCreatedBy().getName() : "Admin")
                .createdAt(question.getCreatedAt())
                .isActive(question.getIsActive())
                .build();
    }
    // Add this method to FeedbackService.java
    public Feedback getFeedbackByIdForUpdate(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));
    }
}