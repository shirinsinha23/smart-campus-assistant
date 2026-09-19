package com.example.smartcampusassistant.hostel.enums;

public enum RoomBedType {
    SINGLE_BED("Single Bed"),
    DOUBLE_BED("Double Bed"),
    QUEEN_BED("Queen Bed"),
    KING_BED("King Bed"),
    BUNK_BED("Bunk Bed"),
    SOFA_CUM_BED("Sofa-cum-Bed");

    private final String displayName;

    RoomBedType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}