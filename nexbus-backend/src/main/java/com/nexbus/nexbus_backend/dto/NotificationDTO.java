package com.nexbus.nexbus_backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public class NotificationDTO {
    private Integer notificationId;
    private Integer userId;

    @Size(max = 255, message = "Message must not exceed 255 characters")
    private String message;

    private Boolean isRead;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Integer getNotificationId() { return notificationId; }
    public void setNotificationId(Integer notificationId) { this.notificationId = notificationId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}