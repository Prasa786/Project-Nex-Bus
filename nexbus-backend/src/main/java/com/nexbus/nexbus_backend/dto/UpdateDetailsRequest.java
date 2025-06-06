package com.nexbus.nexbus_backend.dto;

   import lombok.Data;

   @Data
   public class UpdateDetailsRequest {
       private String email;
       private String roleName;
   }