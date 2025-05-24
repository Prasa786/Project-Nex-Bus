<<<<<<< HEAD
package com.nexbus.nexbus_backend.dto;

import lombok.Data;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Data
public class DriverDTO {
    private Integer driverId;

    @NotBlank(message = "Driver name is required")
    private String driverName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid contact number")
    private String contactNumber;

    @NotBlank(message = "License number cannot be blank")
    private String licenseNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
=======
package com.nexbus.nexbus_backend.dto;

import lombok.Data;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Data
public class DriverDTO {
    private Integer driverId;

    @NotBlank(message = "Driver name is required")
    private String driverName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid contact number")
    private String contactNumber;

    @NotBlank(message = "License number cannot be blank")
    private String licenseNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}