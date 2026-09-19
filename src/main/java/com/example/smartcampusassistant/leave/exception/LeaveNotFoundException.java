package com.example.smartcampusassistant.leave.exception;

public class LeaveNotFoundException extends RuntimeException {
    public LeaveNotFoundException(String message) {
        super(message);
    }

    public LeaveNotFoundException(Long leaveId) {
        super("Leave not found with id: " + leaveId);
    }
}