// DriveApplicationDTO.java
package com.example.smartcampusassistant.placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriveApplicationDTO {
    private Long id;
    private Long driveId;
    private String driveTitle;
    private String companyName;
    private Long studentId;
    private String studentName;
    private String studentLoginId;
    private String resumeUrl;
    private String coverLetter;
    private String status;
    private String statusDisplay;
    private String statusColor;
    private LocalDateTime appliedAt;
    private String remarks;
    private InterviewScheduleDTO interview;
}