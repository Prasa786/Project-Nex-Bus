package com.nexbus.nexbus_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private Integer scheduleId;

    @NotNull(message = "Bus ID cannot be null")
    @Min(value = 1, message = "Bus ID must be positive")
    private Integer busId;
    @NotNull(message = "Departure time cannot be null")

    private LocalDateTime departureTime;
    @NotNull(message = "Arrival time cannot be null")
    private LocalDateTime arrivalTime;
    private BigDecimal fare;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}