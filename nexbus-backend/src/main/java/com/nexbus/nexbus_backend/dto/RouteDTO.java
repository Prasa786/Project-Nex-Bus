<<<<<<< HEAD
package com.nexbus.nexbus_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RouteDTO {
    private Integer routeId;

    @NotBlank(message = "Route name cannot be blank")
    private String routeName;

    @NotBlank(message = "Start location cannot be blank")
    private String startLocation;

    @NotBlank(message = "End location cannot be blank")
    private String endLocation;

    @NotNull(message = "Distance cannot be null")
    @Min(value = 1, message = "Distance must be positive")
    private Double distance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
=======
package com.nexbus.nexbus_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RouteDTO {
    private Integer routeId;

    @NotBlank(message = "Route name cannot be blank")
    private String routeName;

    @NotBlank(message = "Start location cannot be blank")
    private String startLocation;

    @NotBlank(message = "End location cannot be blank")
    private String endLocation;

    @NotNull(message = "Distance cannot be null")
    @Min(value = 1, message = "Distance must be positive")
    private Double distance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}