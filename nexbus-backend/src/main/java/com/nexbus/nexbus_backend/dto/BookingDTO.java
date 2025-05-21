package com.nexbus.nexbus_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Integer bookingId;
    private Integer userId;
    private Integer busId;
    private Integer seatId;
    private Integer scheduleId;
    private LocalDateTime bookingDate;
    private String status;
    private Double fare;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}