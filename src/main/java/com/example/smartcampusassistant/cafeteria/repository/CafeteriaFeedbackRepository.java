package com.example.smartcampusassistant.cafeteria.repository;

import com.example.smartcampusassistant.cafeteria.model.CafeteriaFeedback;  // ← Updated import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CafeteriaFeedbackRepository extends JpaRepository<CafeteriaFeedback, Long> {  // ← Updated entity type

    List<CafeteriaFeedback> findByUserId(Long userId);

    List<CafeteriaFeedback> findByOrderId(Long orderId);

    List<CafeteriaFeedback> findByMealItemId(Long mealItemId);

    @Query("SELECT AVG(f.rating) FROM CafeteriaFeedback f WHERE f.mealItemId = :mealItemId")  // ← Updated entity name
    Double getAverageRatingForMealItem(@Param("mealItemId") Long mealItemId);

    @Query("SELECT f.mealItemId, AVG(f.rating) FROM CafeteriaFeedback f GROUP BY f.mealItemId ORDER BY AVG(f.rating) DESC")  // ← Updated entity name
    List<Object[]> getTopRatedItems();

    @Query("SELECT COUNT(f) FROM CafeteriaFeedback f WHERE f.rating >= 4")  // ← Updated entity name
    long countPositiveFeedbacks();
}