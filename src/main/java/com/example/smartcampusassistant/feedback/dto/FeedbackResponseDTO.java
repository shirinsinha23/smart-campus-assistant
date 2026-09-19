package com.example.smartcampusassistant.feedback.dto;

import com.example.smartcampusassistant.feedback.enums.FeedbackCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDTO {

    private Long id;
    private String studentName;
    private String studentLoginId;
    private String title;
    private String comment;
    private Integer rating;
    private FeedbackCategory category;
    private String subject;
    private Long facultyId;
    private String courseName;
    private Boolean isAnonymous;
    private LocalDateTime createdAt;
    private Double averageRatingForCategory;
}