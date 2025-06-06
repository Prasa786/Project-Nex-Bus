package com.nexbus.nexbus_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

@Data
public class UserDTO {
    @NotBlank private Integer userId;
    @NotBlank private String email;
    private String firstName;
    private String lastName;
    @NotBlank private String phoneNumber;
    private String address;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}