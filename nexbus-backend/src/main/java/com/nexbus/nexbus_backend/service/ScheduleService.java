package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.ScheduleDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.Schedule;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, BusRepository busRepository) {
        this.scheduleRepository = scheduleRepository;
        this.busRepository = busRepository;
    }

    public List<ScheduleDTO> findAll() {
        logger.debug("Fetching all schedules");
        List<ScheduleDTO> schedules = scheduleRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} schedules", schedules.size());
        return schedules;
    }

    public ScheduleDTO findById(Integer id) {
        logger.debug("Fetching schedule with ID: {}", id);
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Schedule not found with ID: {}", id);
                return new ResourceNotFoundException("Schedule", "ID", id);
            });
        logger.info("Found schedule with ID: {}", id);
        return mapToDTO(schedule);
    }

    public List<ScheduleDTO> findByBusId(Integer busId) {
        logger.debug("Fetching schedules for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<ScheduleDTO> schedules = scheduleRepository.findByBus(bus).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} schedules for bus ID: {}", schedules.size(), busId);
        return schedules;
    }

    @Transactional
    public ScheduleDTO save(ScheduleDTO scheduleDTO) {
        logger.debug("Saving new schedule for bus ID: {}", scheduleDTO.getBusId());
        Bus bus = busRepository.findById(scheduleDTO.getBusId())
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", scheduleDTO.getBusId());
                return new ResourceNotFoundException("Bus", "ID", scheduleDTO.getBusId());
            });
        Schedule schedule = new Schedule();
        schedule.setBus(bus);
        schedule.setDepartureTime(scheduleDTO.getDepartureTime());
        schedule.setArrivalTime(scheduleDTO.getArrivalTime());
        schedule = scheduleRepository.save(schedule);
        logger.info("Saved schedule with ID: {}", schedule.getScheduleId());
        return mapToDTO(schedule);
    }

    @Transactional
    public ScheduleDTO update(Integer id, ScheduleDTO scheduleDTO) {
        logger.debug("Updating schedule with ID: {}", id);
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Schedule not found with ID: {}", id);
                return new ResourceNotFoundException("Schedule", "ID", id);
            });
        Bus bus = busRepository.findById(scheduleDTO.getBusId())
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", scheduleDTO.getBusId());
                return new ResourceNotFoundException("Bus", "ID", scheduleDTO.getBusId());
            });
        schedule.setBus(bus);
        schedule.setDepartureTime(scheduleDTO.getDepartureTime());
        schedule.setArrivalTime(scheduleDTO.getArrivalTime());
        schedule = scheduleRepository.save(schedule);
        logger.info("Updated schedule with ID: {}", id);
        return mapToDTO(schedule);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting schedule with ID: {}", id);
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Schedule not found with ID: {}", id);
                return new ResourceNotFoundException("Schedule", "ID", id);
            });
        scheduleRepository.delete(schedule);
        logger.info("Deleted schedule with ID: {}", id);
    }

    private ScheduleDTO mapToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setBusId(schedule.getBus().getBusId());
        dto.setDepartureTime(schedule.getDepartureTime());
        dto.setArrivalTime(schedule.getArrivalTime());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setUpdatedAt(schedule.getUpdatedAt());
        return dto;
    }
}