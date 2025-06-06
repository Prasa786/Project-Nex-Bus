package com.nexbus.nexbus_backend.exception;



public class UserAlreadyExistsException extends IllegalArgumentException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}