<<<<<<< HEAD
package com.nexbus.nexbus_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_fares")
@Data
public class PointsFares {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pointFareId;

    @Column(name = "schedule_id", nullable = false)
    private Integer scheduleId;

    @Column(length = 100)
    private String boardingPoint;

    @Column(length = 100)
    private String droppingPoint;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fare;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
=======
package com.nexbus.nexbus_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_fares")
@Data
public class PointsFares {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pointFareId;

    @Column(name = "schedule_id", nullable = false)
    private Integer scheduleId;

    @Column(length = 100)
    private String boardingPoint;

    @Column(length = 100)
    private String droppingPoint;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fare;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}