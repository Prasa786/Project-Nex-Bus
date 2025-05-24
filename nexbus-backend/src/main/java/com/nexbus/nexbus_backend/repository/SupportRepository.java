package com.nexbus.nexbus_backend.repository;

   import com.nexbus.nexbus_backend.model.Support;
   import org.springframework.data.jpa.repository.JpaRepository;

   public interface SupportRepository extends JpaRepository<Support, Integer> {
   }