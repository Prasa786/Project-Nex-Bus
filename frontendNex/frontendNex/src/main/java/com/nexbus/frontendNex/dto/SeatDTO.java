package com.nexbus.frontendNex.dto;

import java.time.LocalDateTime;

public class SeatDTO {
    private Integer seatId;
    private Integer busId;
    private String seatNumber;
    private Boolean isAvailable;
    private String seatType;
    private LocalDateTime createdAt;

    // Getters and setters
    public Integer getSeatId() { return seatId; }
    public void setSeatId(Integer seatId) { this.seatId = seatId; }
    public Integer getBusId() { return busId; }
    public void setBusId(Integer busId) { this.busId = busId; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}