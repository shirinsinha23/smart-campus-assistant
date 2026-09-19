package com.example.smartcampusassistant.lostandfound.dto;

import com.example.smartcampusassistant.lostandfound.entity.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LostItemResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String imageUrl;
    private String location;
    private LocalDateTime date;
    private ItemStatus status;
    private Long reportedById;
    private String reportedByName;
    private Long claimedById;
    private String claimedByName;
    private String contactInfo;
    private LocalDateTime createdAt;
}