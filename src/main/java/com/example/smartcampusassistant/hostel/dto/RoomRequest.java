package com.example.smartcampusassistant.hostel.dto;

import com.example.smartcampusassistant.hostel.enums.RoomBedType;
import com.example.smartcampusassistant.hostel.enums.RoomFacility;
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
public class RoomRequest {
    private String roomNumber;
    private Integer floor;
    private String wing;
    private RoomType roomType;
    private RoomBedType bedType;
    private Integer totalRooms;
    private Integer capacity;
    private Integer attachedBathrooms;
    private Integer attachedWashrooms;
    private Double rentPerMonth;
    private String description;
    private List<RoomFacility> facilities;
    private String additionalAmenities;
    private Double roomSize;
    private Boolean hasBalcony;
    private Boolean hasKitchen;
    private Boolean hasLivingRoom;
    private String furnishedType;
}