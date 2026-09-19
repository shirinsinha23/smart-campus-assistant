package com.example.smartcampusassistant.cafeteria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealItemDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String mealType;
    private Integer preparationTime;
    private Boolean isAvailable;
}