package com.nexbus.nexbus_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @Column(name = "operator_id", nullable = false)
    private Integer operatorId;  // Store operator ID directly

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;  // Proper JPA relationship for Bus

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;
    
    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentType;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column
    private String description;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
    
    

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}