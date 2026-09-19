package com.example.smartcampusassistant.hostel.enums;

public enum RoomFacility {
    ATTACHED_BATHROOM("Attached Bathroom"),
    ATTACHED_WASHROOM("Attached Washroom"),
    AC("Air Conditioning"),
    WIFI("WiFi"),
    FURNISHED("Fully Furnished"),
    STUDY_TABLE("Study Table"),
    WARDROBE("Wardrobe"),
    GEYSER("Geyser"),
    BALCONY("Balcony"),
    KITCHEN("Kitchen"),
    LIVING_ROOM("Living Room"),
    PARKING("Parking");

    private final String displayName;

    RoomFacility(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}