package com.example.smartcampusassistant.hostel.enums;

public enum AllocationStatus {
    ACTIVE("Active"),
    CHECKED_OUT("Checked Out"),
    TRANSFERRED("Transferred"),
    CANCELLED("Cancelled");

    private final String displayName;

    AllocationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}