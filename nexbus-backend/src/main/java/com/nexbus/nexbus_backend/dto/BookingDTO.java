<<<<<<< HEAD
package com.nexbus.nexbus_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Integer bookingId;

    @NotNull(message = "Bus ID cannot be null")
    @Min(value = 1, message = "Bus ID must be positive")
    private Integer busId;

    @NotNull(message = "User ID cannot be null")
    @Min(value = 1, message = "User ID must be positive")
    private Integer userId;

    @NotNull(message = "Seat ID cannot be null")
    @Min(value = 1, message = "Seat ID must be positive")
    private Integer seatId;

    @NotNull(message = "Schedule ID cannot be null")
    @Min(value = 1, message = "Schedule ID must be positive")
    private Integer scheduleId;

    @NotNull(message = "Booking date cannot be null")
    private LocalDateTime bookingDate;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    @NotNull(message = "Fare cannot be null")
    @Min(value = 0, message = "Fare cannot be negative")
    private BigDecimal fare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
=======
package com.nexbus.nexbus_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Integer bookingId;
    private Integer userId;
    private Integer busId;
    private Integer seatId;
    private Integer scheduleId;
    private LocalDateTime bookingDate;
    private String status;
    private Double fare;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}