package com.example.smartcampusassistant.cafeteria.repository;

import com.example.smartcampusassistant.cafeteria.model.MealType;
import com.example.smartcampusassistant.cafeteria.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByDate(LocalDate date);

    List<Menu> findByDateAndMealType(LocalDate date, MealType mealType);

    Optional<Menu> findByDateAndMealTypeAndIsActiveTrue(LocalDate date, MealType mealType);

    @Query("SELECT m FROM Menu m WHERE m.date = :date AND m.isActive = true")
    List<Menu> findActiveMenusByDate(@Param("date") LocalDate date);

    @Query("SELECT m FROM Menu m WHERE m.date BETWEEN :startDate AND :endDate")
    List<Menu> findMenusBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Menu> findByIsActiveTrue();
}