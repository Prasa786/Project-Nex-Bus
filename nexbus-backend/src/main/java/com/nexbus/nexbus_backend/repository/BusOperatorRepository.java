package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.BusOperator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusOperatorRepository extends JpaRepository<BusOperator, Integer> {
    BusOperator findByEmail(String email);
    
}