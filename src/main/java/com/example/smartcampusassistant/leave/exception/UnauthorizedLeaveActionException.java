package com.example.smartcampusassistant.leave.exception;

public class UnauthorizedLeaveActionException extends RuntimeException {
    public UnauthorizedLeaveActionException(String message) {
        super(message);
    }
}