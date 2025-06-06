package com.nexbus.nexbus_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleDTO {
    private Integer roleId;
    private String roleName;
    private LocalDateTime createdAt;
}
