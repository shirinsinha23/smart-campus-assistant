package com.example.smartcampusassistant.hostel.dto;

import com.example.smartcampusassistant.hostel.enums.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequestDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private MaintenanceStatus status;
    private String statusDisplay;
    private Long reportedById;
    private String reportedByName;
    private Long roomId;
    private String roomNumber;
    private String resolutionNotes;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
}