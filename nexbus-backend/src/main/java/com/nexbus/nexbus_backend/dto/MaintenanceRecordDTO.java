package com.nexbus.nexbus_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaintenanceRecordDTO {
    private Integer maintenanceId;

    @NotNull(message = "Bus ID cannot be null")
    @Min(value = 1, message = "Bus ID must be positive")
    private Integer busId;

    @NotNull(message = "Maintenance date cannot be null")
    private LocalDateTime maintenanceDate;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Cost cannot be null")
    @Min(value = 0, message = "Cost cannot be negative")
    private BigDecimal cost;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}