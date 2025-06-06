package com.nexbus.nexbus_backend.dto;

import com.nexbus.nexbus_backend.model.Seat.SeatType;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Data
public class SeatDTO {
    private Integer seatId;

    @NotNull(message = "Bus ID is required")
    @Min(value = 1, message = "Bus ID must be positive")
    private Integer busId;

    @NotBlank(message = "Seat number is required")
    @Size(min = 1, max = 10, message = "Seat number must be between 1 and 10 characters")
    private String seatNumber;

    private Boolean isAvailable;

    private SeatType seatType;

    private LocalDateTime createdAt;
}