package com.example.smartcampusassistant.feedback.enums;

public enum FeedbackCategory {
    FACULTY("Faculty"),
    COURSES("Courses"),
    INFRASTRUCTURE("Infrastructure"),
    CAFETERIA("Cafeteria"),
    HOSTEL("Hostel");

    private final String displayName;

    FeedbackCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}