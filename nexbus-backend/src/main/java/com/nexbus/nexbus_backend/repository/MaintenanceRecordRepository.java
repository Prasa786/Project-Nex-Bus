package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Integer> {
    List<MaintenanceRecord> findByBus(Bus bus);
}