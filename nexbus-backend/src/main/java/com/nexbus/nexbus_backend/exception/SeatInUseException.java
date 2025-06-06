package com.nexbus.nexbus_backend.exception;

public class SeatInUseException extends RuntimeException {
    public SeatInUseException(String message) {
        super(message);
    }
}