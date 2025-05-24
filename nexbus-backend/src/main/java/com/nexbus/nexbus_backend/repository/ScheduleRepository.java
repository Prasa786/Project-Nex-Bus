package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByBusBusId(Integer busId);
    List<Schedule> findByBus(Bus bus);
    

}