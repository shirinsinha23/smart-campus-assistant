// InterviewScheduleDTO.java
package com.example.smartcampusassistant.placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewScheduleDTO {
    private Long id;
    private Long applicationId;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private String mode;
    private String modeDisplay;
    private String venue;
    private String meetingLink;
    private String interviewPanel;
    private String status;
    private String statusDisplay;
    private String feedback;
    private String result;
}