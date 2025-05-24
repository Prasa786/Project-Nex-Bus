<<<<<<< HEAD
package com.nexbus.nexbus_backend.exception;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(String seatNumber, Integer busId) {
        super(String.format("Seat %s on bus ID %d is already booked", seatNumber, busId));
    }
=======
package com.nexbus.nexbus_backend.exception;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(String seatNumber, Integer busId) {
        super(String.format("Seat %s on bus ID %d is already booked", seatNumber, busId));
    }
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}