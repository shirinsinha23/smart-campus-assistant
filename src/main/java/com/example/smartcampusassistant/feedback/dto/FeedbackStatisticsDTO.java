package com.example.smartcampusassistant.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStatisticsDTO {
    private Long totalFeedbacks;
    private Map<String, Double> averageRatingsByCategory;
    private Map<String, Long> feedbackCountByCategory;
    private Double overallAverageRating;
    private Long totalStudentsWhoGaveFeedback;
}