package com.example.smartcampusassistant.transport.enums;

public enum SeatStatus {
    AVAILABLE("Available"),
    BOOKED("Booked"),
    RESERVED("Reserved");

    private final String displayName;

    SeatStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}