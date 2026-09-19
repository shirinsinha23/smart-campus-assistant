// MealItemRepository.java - Add restaurant queries
package com.example.smartcampusassistant.cafeteria.repository;

import com.example.smartcampusassistant.cafeteria.model.MealItem;
import com.example.smartcampusassistant.cafeteria.model.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealItemRepository extends JpaRepository<MealItem, Long> {

    List<MealItem> findByMealType(MealType mealType);

    List<MealItem> findByIsAvailableTrue();

    @Query("SELECT m FROM MealItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MealItem> searchByName(@Param("keyword") String keyword);

    List<MealItem> findByCategory(String category);

    // ✅ NEW: Get items by restaurant
    List<MealItem> findByRestaurantId(Long restaurantId);

    List<MealItem> findByRestaurantIdAndIsAvailableTrue(Long restaurantId);

    List<MealItem> findByRestaurantIdAndMealType(Long restaurantId, MealType mealType);

    @Query("SELECT m FROM MealItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true")
    List<MealItem> findAvailableByRestaurant(@Param("restaurantId") Long restaurantId);
}