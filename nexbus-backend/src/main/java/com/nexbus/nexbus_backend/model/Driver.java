package com.nexbus.nexbus_backend.model;

import jakarta.persistence.*;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer driverId;

    @NotBlank(message = "Driver name is required")
    @Column(nullable = false, length = 100)
    private String driverName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid contact number")
    @Column(length = 20)
    private String contactNumber;

    @Column(length = 50)
    private String licenseNumber;

    @Column
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

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







