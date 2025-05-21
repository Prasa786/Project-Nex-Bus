package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.BusOperator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusOperatorRepository extends JpaRepository<BusOperator, Integer> {
    Optional<BusOperator> findByEmail(String email);
}