package com.nexbus.nexbus_backend.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String email;
    private String role;
}