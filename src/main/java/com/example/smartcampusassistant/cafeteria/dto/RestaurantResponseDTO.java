package com.example.smartcampusassistant.cafeteria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String category;
    private String address;
    private String phoneNumber;
    private String email;
    private String openingTime;
    private String closingTime;
    private Boolean isActive;
    private Long ownerId;
    private String ownerName;
    private List<MealItemDTO> items;
}