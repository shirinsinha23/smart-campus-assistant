package com.example.smartcampusassistant.event.dto;

import com.example.smartcampusassistant.event.model.Event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String venue;
    private Integer capacity;
    private Integer registeredCount;
    private String imageUrl;
    private EventStatus status;
    private ClubDTO club;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClubDTO {
        private Long id;
        private String name;
        private String category;
    }
}