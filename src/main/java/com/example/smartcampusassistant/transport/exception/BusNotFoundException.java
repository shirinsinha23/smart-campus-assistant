package com.example.smartcampusassistant.transport.exception;

public class BusNotFoundException extends RuntimeException {
    public BusNotFoundException(String message) {
        super(message);
    }

    public BusNotFoundException(Long busId) {
        super("Bus not found with id: " + busId);
    }
}