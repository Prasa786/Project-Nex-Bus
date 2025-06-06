package com.nexbus.nexbus_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PassengerDTO {
    private Integer userId;
    private String username; // Aligns with UserDetails terminology, maps to email
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}