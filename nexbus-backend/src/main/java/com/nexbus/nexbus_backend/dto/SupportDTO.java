package com.nexbus.nexbus_backend.dto;

   import lombok.Data;

   import java.time.LocalDateTime;

   @Data
   public class SupportDTO {
       private Integer supportId;
       private Integer userId;
       private String subject;
       private String description;
       private String status;
       private LocalDateTime createdAt;
       private LocalDateTime updatedAt;
   }