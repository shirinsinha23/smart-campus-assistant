package com.example.smartcampusassistant.transport.enums;

public enum BusStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    MAINTENANCE("Under Maintenance"),
    OUT_OF_SERVICE("Out of Service");

    private final String displayName;

    BusStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}