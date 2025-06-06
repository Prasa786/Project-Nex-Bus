package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Integer> {
}