<<<<<<< HEAD
package com.nexbus.nexbus_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Integer paymentId;
    
    @NotNull(message = "Operator ID cannot be null")
    @Min(value = 1, message = "Operator ID must be positive")
    private Integer operatorId;  // Should be Integer, not BusOperator
    
    @Min(value = 1, message = "Bus ID must be positive")
    private Integer busId;  // Should be Integer, not Bus
    
    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount cannot be negative")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment type cannot be blank")
    private String paymentType;
    
    @NotNull(message = "Payment date cannot be null")
    private LocalDateTime paymentDate;
    
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
=======
package com.nexbus.nexbus_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Integer paymentId;
    
    @NotNull(message = "Operator ID cannot be null")
    @Min(value = 1, message = "Operator ID must be positive")
    private Integer operatorId;  // Should be Integer, not BusOperator
    
    @Min(value = 1, message = "Bus ID must be positive")
    private Integer busId;  // Should be Integer, not Bus
    
    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount cannot be negative")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment type cannot be blank")
    private String paymentType;
    
    @NotNull(message = "Payment date cannot be null")
    private LocalDateTime paymentDate;
    
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}