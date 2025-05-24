package com.nexbus.nexbus_backend.exception;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(String seatNumber, Integer busId) {
        super(String.format("Seat %s on bus ID %d is already booked", seatNumber, busId));
    }
}