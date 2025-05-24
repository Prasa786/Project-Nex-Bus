package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByOperatorId(Integer operatorId);
    List<Payment> findByBus_BusId(Integer busId);  // Correct JPA query method
}