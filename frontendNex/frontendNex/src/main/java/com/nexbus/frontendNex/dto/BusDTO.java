package com.nexbus.frontendNex.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BusDTO {
    private Integer busId;
    private Integer operatorId;
    private Integer routeId;
    private String busNumber;
    private Integer totalSeats;
    private List<AmenitiesDTO> amenities;
    private List<MaintenanceRecordDTO> maintenanceRecords;
    private DriverDTO driver;
    private List<ScheduleDTO> schedules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and setters
    public Integer getBusId() { return busId; }
    public void setBusId(Integer busId) { this.busId = busId; }
    public Integer getOperatorId() { return operatorId; }
    public void setOperatorId(Integer operatorId) { this.operatorId = operatorId; }
    public Integer getRouteId() { return routeId; }
    public void setRouteId(Integer routeId) { this.routeId = routeId; }
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public List<AmenitiesDTO> getAmenities() { return amenities; }
    public void setAmenities(List<AmenitiesDTO> amenities) { this.amenities = amenities; }
    public List<MaintenanceRecordDTO> getMaintenanceRecords() { return maintenanceRecords; }
    public void setMaintenanceRecords(List<MaintenanceRecordDTO> maintenanceRecords) { this.maintenanceRecords = maintenanceRecords; }
    public DriverDTO getDriver() { return driver; }
    public void setDriver(DriverDTO driver) { this.driver = driver; }
    public List<ScheduleDTO> getSchedules() { return schedules; }
    public void setSchedules(List<ScheduleDTO> schedules) { this.schedules = schedules; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}