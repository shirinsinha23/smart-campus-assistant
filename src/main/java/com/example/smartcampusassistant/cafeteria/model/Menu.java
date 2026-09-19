package com.example.smartcampusassistant.cafeteria.model;

import com.example.smartcampusassistant.cafeteria.MealType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @ManyToMany
    @JoinTable(
            name = "menu_meal_items",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_item_id")
    )
    @Builder.Default
    private List<com.example.smartcampusassistant.cafeteria.model.MealItem> items = new ArrayList<>();

    @Builder.Default
    private Boolean isActive = true;

    private String specialNote;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;
}