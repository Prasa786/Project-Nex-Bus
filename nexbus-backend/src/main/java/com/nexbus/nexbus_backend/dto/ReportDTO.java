package com.nexbus.nexbus_backend.dto;

   import lombok.Data;

   import java.util.List;

   @Data
   public class ReportDTO {
       private String reportType;
       private List<String> headers;
       private List<List<String>> rows;
   }