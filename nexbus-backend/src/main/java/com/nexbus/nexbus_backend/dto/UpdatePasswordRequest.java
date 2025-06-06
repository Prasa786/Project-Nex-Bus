package com.nexbus.nexbus_backend.dto;

   import lombok.Data;

   @Data
   public class UpdatePasswordRequest {
       private String currentPassword;
       private String newPassword;
   }