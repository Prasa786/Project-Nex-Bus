package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.MaintenanceRecordDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.MaintenanceRecord;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.MaintenanceRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final BusRepository busRepository;

    public MaintenanceService(MaintenanceRecordRepository maintenanceRecordRepository, BusRepository busRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.busRepository = busRepository;
    }

    public List<MaintenanceRecordDTO> findAll() {
        logger.debug("Fetching all maintenance records");
        List<MaintenanceRecordDTO> records = maintenanceRecordRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} maintenance records", records.size());
        return records;
    }

    public MaintenanceRecordDTO findById(Integer id) {
        logger.debug("Fetching maintenance record with ID: {}", id);
        MaintenanceRecord record = maintenanceRecordRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Maintenance record not found with ID: {}", id);
                return new ResourceNotFoundException("MaintenanceRecord", "ID", id);
            });
        logger.info("Found maintenance record with ID: {}", id);
        return mapToDTO(record);
    }

    public List<MaintenanceRecordDTO> findByBusId(Integer busId) {
        logger.debug("Fetching maintenance records for bus ID: {}", busId);
        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", busId);
                return new ResourceNotFoundException("Bus", "ID", busId);
            });
        List<MaintenanceRecordDTO> records = maintenanceRecordRepository.findByBus(bus).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} maintenance records for bus ID: {}", records.size(), busId);
        return records;
    }

    @Transactional
    public MaintenanceRecordDTO save(MaintenanceRecordDTO recordDTO) {
        logger.debug("Saving new maintenance record for bus ID: {}", recordDTO.getBusId());
        Bus bus = busRepository.findById(recordDTO.getBusId())
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", recordDTO.getBusId());
                return new ResourceNotFoundException("Bus", "ID", recordDTO.getBusId());
            });
        MaintenanceRecord record = new MaintenanceRecord();
        record.setBus(bus);
        record.setMaintenanceDate(recordDTO.getMaintenanceDate());
        record.setDescription(recordDTO.getDescription());
        record.setCost(recordDTO.getCost());
        record = maintenanceRecordRepository.save(record);
        logger.info("Saved maintenance record with ID: {}", record.getMaintenanceId());
        return mapToDTO(record);
    }

    @Transactional
    public MaintenanceRecordDTO update(Integer id, MaintenanceRecordDTO recordDTO) {
        logger.debug("Updating maintenance record with ID: {}", id);
        MaintenanceRecord record = maintenanceRecordRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Maintenance record not found with ID: {}", id);
                return new ResourceNotFoundException("MaintenanceRecord", "ID", id);
            });
        Bus bus = busRepository.findById(recordDTO.getBusId())
            .orElseThrow(() -> {
                logger.error("Bus not found with ID: {}", recordDTO.getBusId());
                return new ResourceNotFoundException("Bus", "ID", recordDTO.getBusId());
            });
        record.setBus(bus);
        record.setMaintenanceDate(recordDTO.getMaintenanceDate());
        record.setDescription(recordDTO.getDescription());
        record.setCost(recordDTO.getCost());
        record = maintenanceRecordRepository.save(record);
        logger.info("Updated maintenance record with ID: {}", id);
        return mapToDTO(record);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting maintenance record with ID: {}", id);
        MaintenanceRecord record = maintenanceRecordRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Maintenance record not found with ID: {}", id);
                return new ResourceNotFoundException("MaintenanceRecord", "ID", id);
            });
        maintenanceRecordRepository.delete(record);
        logger.info("Deleted maintenance record with ID: {}", id);
    }

    private MaintenanceRecordDTO mapToDTO(MaintenanceRecord record) {
        MaintenanceRecordDTO dto = new MaintenanceRecordDTO();
        dto.setMaintenanceId(record.getMaintenanceId());
        dto.setBusId(record.getBus().getBusId());
        dto.setMaintenanceDate(record.getMaintenanceDate());
        dto.setDescription(record.getDescription());
        dto.setCost(record.getCost());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }
}