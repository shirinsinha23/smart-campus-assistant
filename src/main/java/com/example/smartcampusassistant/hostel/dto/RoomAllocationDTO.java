package com.example.smartcampusassistant.hostel.dto;

import com.example.smartcampusassistant.hostel.enums.AllocationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAllocationDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentLoginId;
    private Long roomId;
    private String roomNumber;
    private String hostelName;
    private String hostelBlock;
    private LocalDate allocatedDate;
    private LocalDate checkOutDate;
    private AllocationStatus status;
    private String statusDisplay;
    private String remarks;
}