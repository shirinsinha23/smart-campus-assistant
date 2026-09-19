package com.example.smartcampusassistant.transport.exception;

public class BusNotFoundException extends RuntimeException {
  public BusNotFoundException(String message) {
    super(message);
  }
}
