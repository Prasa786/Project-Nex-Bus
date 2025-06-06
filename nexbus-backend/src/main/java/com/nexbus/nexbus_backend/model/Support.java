package com.nexbus.nexbus_backend.model;

   import jakarta.persistence.*;
   import lombok.Data;

   import java.time.LocalDateTime;

   @Entity
   @Table(name = "support")
   @Data
   public class Support {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       @Column(name = "support_id")
       private Integer supportId;

       @ManyToOne(fetch = FetchType.LAZY)
       @JoinColumn(name = "user_id", nullable = false)
       private User user;

       @Column(name = "subject", nullable = false)
       private String subject;

       @Column(name = "description", nullable = false, columnDefinition = "TEXT")
       private String description;

       @Column(name = "status", nullable = false)
       private String status;

       @Column(name = "created_at", updatable = false)
       private LocalDateTime createdAt;

       @Column(name = "updated_at")
       private LocalDateTime updatedAt;

       @PrePersist
       protected void onCreate() {
           createdAt = LocalDateTime.now();
           updatedAt = LocalDateTime.now();
           if (status == null) {
               status = "OPEN";
           }
       }

       @PreUpdate
       protected void onUpdate() {
           updatedAt = LocalDateTime.now();
       }
   }