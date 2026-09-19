package com.example.smartcampusassistant.hostel.dto;

import com.example.smartcampusassistant.hostel.enums.RoomBedType;
import com.example.smartcampusassistant.hostel.enums.RoomFacility;
import com.example.smartcampusassistant.hostel.enums.RoomStatus;
import com.example.smartcampusassistant.hostel.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Long id;
    private String roomNumber;
    private Integer floor;
    private String wing;
    private RoomType roomType;
    private String roomTypeDisplay;
    private RoomBedType bedType;
    private String bedTypeDisplay;
    private Integer totalRooms;
    private Integer capacity;
    private Integer availableSeats;
    private Integer occupiedCount;  // ← Add this field
    private Integer attachedBathrooms;
    private Integer attachedWashrooms;
    private Double rentPerMonth;
    private String description;
    private List<RoomFacility> facilities;
    private List<String> facilityDisplayNames;
    private String additionalAmenities;
    private RoomStatus status;
    private String statusDisplay;
    private Double roomSize;
    private Boolean hasBalcony;
    private Boolean hasKitchen;
    private Boolean hasLivingRoom;
    private String furnishedType;
    private Long hostelId;
    private String hostelName;
    private String hostelBlock;
}