package com.example.smartcampusassistant.hostel.enums;

public enum RoomType {
    SINGLE_BED("Single Bed Room", 1),
    DOUBLE_BED("Double Bed Room", 2),
    TRIPLE_BED("Triple Bed Room", 3),
    TWO_BHK("2 BHK Apartment", 4),
    THREE_BHK("3 BHK Apartment", 5),
    FIVE_BHK("5 BHK Apartment", 6);

    private final String displayName;
    private final int defaultCapacity;

    RoomType(String displayName, int defaultCapacity) {
        this.displayName = displayName;
        this.defaultCapacity = defaultCapacity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }
}