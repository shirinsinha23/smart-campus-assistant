package com.example.smartcampusassistant.cafeteria.service;

import com.example.smartcampusassistant.cafeteria.model.CafeteriaFeedback;
import com.example.smartcampusassistant.cafeteria.repository.CafeteriaFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeteriaFeedbackService {

    private final CafeteriaFeedbackRepository cafeteriaFeedbackRepository;

    @Transactional
    public CafeteriaFeedback saveFeedback(CafeteriaFeedback feedback) {
        log.info("📋 Saving cafeteria feedback for user: {}", feedback.getUser().getId());
        return cafeteriaFeedbackRepository.save(feedback);
    }

    public List<CafeteriaFeedback> getFeedbackByUser(Long userId) {
        log.info("📋 Fetching cafeteria feedback for user: {}", userId);
        return cafeteriaFeedbackRepository.findByUserId(userId);
    }

    public List<CafeteriaFeedback> getFeedbackByMealItem(Long mealItemId) {
        log.info("📋 Fetching cafeteria feedback for meal item: {}", mealItemId);
        return cafeteriaFeedbackRepository.findByMealItemId(mealItemId);
    }

    public Double getAverageRatingForMealItem(Long mealItemId) {
        log.info("📋 Fetching average rating for meal item: {}", mealItemId);
        return cafeteriaFeedbackRepository.getAverageRatingForMealItem(mealItemId);
    }
}