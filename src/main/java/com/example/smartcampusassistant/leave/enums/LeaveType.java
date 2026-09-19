package com.example.smartcampusassistant.leave.enums;

public enum LeaveType {
    SICK("Sick Leave"),
    CASUAL("Casual Leave"),
    EMERGENCY("Emergency Leave"),
    VACATION("Vacation Leave"),
    STUDY("Study Leave"),
    OTHER("Other Leave");

    private final String displayName;

    LeaveType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}