package com.nexbus.frontendNex.dto;

import java.time.LocalDateTime;

public class BookingDTO {
    private Integer bookingId;
    private Integer userId;
    private Integer seatId;
    private Integer scheduleId;
    private Integer busId;
    private Double fare;
    private String status;
    private LocalDateTime bookingDate;

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getSeatId() { return seatId; }
    public void setSeatId(Integer seatId) { this.seatId = seatId; }
    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
    public Integer getBusId() { return busId; }
    public void setBusId(Integer busId) { this.busId = busId; }
    public Double getFare() { return fare; }
    public void setFare(Double fare) { this.fare = fare; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
}