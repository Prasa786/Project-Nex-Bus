package com.nexbus.nexbus_backend.dto;

   import lombok.Data;

   @Data
   public class AnalyticsDTO {
       private Long totalConfirmedBookings;
       private Double totalRevenue;
       private Long totalPassengers;
   }