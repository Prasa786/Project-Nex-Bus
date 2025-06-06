package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
}