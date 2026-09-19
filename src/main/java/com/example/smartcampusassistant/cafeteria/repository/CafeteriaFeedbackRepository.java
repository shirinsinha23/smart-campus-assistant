package com.example.smartcampusassistant.cafeteria.repository;

import com.example.smartcampusassistant.cafeteria.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByUserId(Long userId);

    List<Feedback> findByOrderId(Long orderId);

    List<Feedback> findByMealItemId(Long mealItemId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.mealItemId = :mealItemId")
    Double getAverageRatingForMealItem(@Param("mealItemId") Long mealItemId);

    @Query("SELECT f.mealItemId, AVG(f.rating) FROM Feedback f GROUP BY f.mealItemId ORDER BY AVG(f.rating) DESC")
    List<Object[]> getTopRatedItems();

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.rating >= 4")
    long countPositiveFeedbacks();
}