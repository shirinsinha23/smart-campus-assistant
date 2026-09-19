package com.example.smartcampusassistant.feedback.dto;

import com.example.smartcampusassistant.feedback.enums.FeedbackCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackQuestionRequest {

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotNull(message = "Category is required")
    private FeedbackCategory category;

    @Builder.Default
    private Boolean isRequired = true;

    private String description;
}