<<<<<<< HEAD
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
=======
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
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
   }