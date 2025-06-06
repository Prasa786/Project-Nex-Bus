package com.nexbus.nexbus_backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {

    private Integer routeId;

    @NotBlank(message = "Route name cannot be blank")
    @Size(min = 3, max = 100, message = "Route name must be between 3 and 100 characters")
    private String routeName;

    @NotBlank(message = "Start location cannot be blank")
    @Size(min = 2, max = 50, message = "Start location must be between 2 and 50 characters")
    private String startLocation;

    @NotBlank(message = "End location cannot be blank")
    @Size(min = 2, max = 50, message = "End location must be between 2 and 50 characters")
    private String endLocation;

    @NotNull(message = "Distance cannot be null")
    @Positive(message = "Distance must be positive")
    private Double distance;
}