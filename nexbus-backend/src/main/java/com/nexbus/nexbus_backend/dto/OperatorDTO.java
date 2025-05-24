<<<<<<< HEAD
package com.nexbus.nexbus_backend.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class OperatorDTO {
    private Integer operatorId;
    private String operatorName;
    private String contactNumber;
    private String email;
    private String address;
    private Integer userId; // New field
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

   
=======
package com.nexbus.nexbus_backend.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class OperatorDTO {
    private Integer operatorId;
    private String operatorName;
    private String contactNumber;
    private String email;
    private String address;
    private Integer userId; // New field
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

   
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}