package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByBusBusId(Integer busId);
    List<Seat> findByBus(Bus bus);
    List<Seat> findByBusAndAvailableTrue(Bus bus);
    Long countByBusBusId(Integer busId);
    Long countByBusBusIdAndAvailableTrue(Integer busId);
    Long countByBusBusIdAndAvailableFalse(Integer busId);
}