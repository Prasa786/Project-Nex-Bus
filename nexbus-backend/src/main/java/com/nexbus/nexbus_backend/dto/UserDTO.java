package com.nexbus.nexbus_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}