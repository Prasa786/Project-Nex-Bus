package com.nexbus.nexbus_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDTO {
    private String type = "about:blank";
    private String title;
    private int status;
    private String detail;
    private String instance;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String title, int status, String detail, String instance) {
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.timestamp = LocalDateTime.now();
    }
}