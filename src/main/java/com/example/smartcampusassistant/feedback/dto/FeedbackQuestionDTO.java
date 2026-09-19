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
public class FeedbackQuestionDTO {

    private Long id;
    private String questionText;
    private FeedbackCategory category;
    private Boolean isRequired;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isActive;
}