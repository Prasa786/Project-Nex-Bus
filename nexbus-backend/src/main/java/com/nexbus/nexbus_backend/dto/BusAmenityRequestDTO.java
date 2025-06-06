package com.nexbus.nexbus_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BusAmenityRequestDTO {
    @NotNull(message = "Bus ID cannot be null")
    @Min(value = 1, message = "Bus ID must be positive")
    private Integer busId;

    @NotNull(message = "Amenity ID cannot be null")
    @Min(value = 1, message = "Amenity ID must be positive")
    private Integer amenityId;
}