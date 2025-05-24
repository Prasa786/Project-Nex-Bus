package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.PointsFares;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointsFaresRepository extends JpaRepository<PointsFares, Integer> {
}