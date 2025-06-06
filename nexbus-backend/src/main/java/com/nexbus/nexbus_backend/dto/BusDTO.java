package com.nexbus.nexbus_backend.dto;

import jakarta.validation.constraints.Min
;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BusDTO {
    private Integer busId;

    @NotNull(message = "Operator ID cannot be null")
    @Min(value = 1, message = "Operator ID must be positive")
    private Integer operatorId;

    
    @NotNull(message = "Route ID cannot be null")
    @Min(value = 1, message = "Route ID must be positive")
    private Integer routeId;

    @NotBlank(message = "Bus number cannot be blank")
    private String busNumber;

    @NotNull(message = "Total seats cannot be null")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;

    private List<AmenitiesDTO> amenities;
    private List<MaintenanceRecordDTO> maintenanceRecords;
    private DriverDTO driver;
    private List<ScheduleDTO> schedules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}