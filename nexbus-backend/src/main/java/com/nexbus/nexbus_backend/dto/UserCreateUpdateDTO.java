package com.nexbus.nexbus_backend.dto;

import lombok.Data;

@Data
public class UserCreateUpdateDTO {
    private Integer userId;
    private String email;
    private String password;
    private String roleName;
}