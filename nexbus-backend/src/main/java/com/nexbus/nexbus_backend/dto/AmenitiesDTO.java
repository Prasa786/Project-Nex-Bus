package com.nexbus.nexbus_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AmenitiesDTO {
    private Integer amenityId;

    @NotBlank(message = "Amenity name cannot be blank")
    private String amenityName;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}