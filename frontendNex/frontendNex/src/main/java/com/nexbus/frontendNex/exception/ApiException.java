package com.nexbus.frontendNex.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}