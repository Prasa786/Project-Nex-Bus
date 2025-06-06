package com.nexbus.frontendNex.dto;

import java.time.LocalDateTime;

public class AmenitiesDTO {
    private Integer amenityId;
    private String amenityName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and setters
    public Integer getAmenityId() { return amenityId; }
    public void setAmenityId(Integer amenityId) { this.amenityId = amenityId; }
    public String getAmenityName() { return amenityName; }
    public void setAmenityName(String amenityName) { this.amenityName = amenityName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}