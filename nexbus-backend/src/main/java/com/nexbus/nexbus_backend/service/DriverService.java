package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.DriverDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Driver;
import com.nexbus.nexbus_backend.repository.DriverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private static final Logger logger = LoggerFactory.getLogger(DriverService.class);

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public List<DriverDTO> findAll() {
        logger.debug("Fetching all drivers");
        List<DriverDTO> drivers = driverRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} drivers", drivers.size());
        return drivers;
    }

    public DriverDTO findById(Integer id) {
        logger.debug("Fetching driver with ID: {}", id);
        Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Driver not found with ID: {}", id);
                return new ResourceNotFoundException("Driver", "ID", id);
            });
        logger.info("Found driver with ID: {}", id);
        return mapToDTO(driver);
    }

    @Transactional
    public DriverDTO save(DriverDTO driverDTO) {
        logger.debug("Saving new driver: {}", driverDTO.getDriverName());
        Driver driver = new Driver();
        driver.setDriverName(driverDTO.getDriverName());
        driver.setContactNumber(driverDTO.getContactNumber());
        driver.setLicenseNumber(driverDTO.getLicenseNumber());
        driver = driverRepository.save(driver);
        logger.info("Saved driver with ID: {}", driver.getDriverId());
        return mapToDTO(driver);
    }

    @Transactional
    public DriverDTO update(Integer id, DriverDTO driverDTO) {
        logger.debug("Updating driver with ID: {}", id);
        Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Driver not found with ID: {}", id);
                return new ResourceNotFoundException("Driver", "ID", id);
            });
        driver.setDriverName(driverDTO.getDriverName());
        driver.setContactNumber(driverDTO.getContactNumber());
        driver.setLicenseNumber(driverDTO.getLicenseNumber());
        driver = driverRepository.save(driver);
        logger.info("Updated driver with ID: {}", id);
        return mapToDTO(driver);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting driver with ID: {}", id);
        Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Driver not found with ID: {}", id);
                return new ResourceNotFoundException("Driver", "ID", id);
            });
        if (isDriverAssigned(id)) {
            logger.warn("Cannot delete driver ID: {} as it is assigned to bus ID: {}", id, getBusIdForDriver(id));
            throw new IllegalStateException("Cannot delete driver as it is assigned to a bus");
        }
        driverRepository.delete(driver);
        logger.info("Deleted driver with ID: {}", id);
    }

    public Integer getBusIdForDriver(Integer driverId) {
        logger.debug("Fetching bus ID for driver ID: {}", driverId);
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> {
                logger.error("Driver not found with ID: {}", driverId);
                return new ResourceNotFoundException("Driver", "ID", driverId);
            });
        Integer busId = driver.getBus() != null ? driver.getBus().getBusId() : null;
        logger.info("Driver ID: {} is assigned to bus ID: {}", driverId, busId != null ? busId : "none");
        return busId;
    }

    public boolean isDriverAssigned(Integer driverId) {
        logger.debug("Checking if driver ID: {} is assigned to a bus", driverId);
        boolean isAssigned = getBusIdForDriver(driverId) != null;
        logger.info("Driver ID: {} is assigned: {}", driverId, isAssigned);
        return isAssigned;
    }

    private DriverDTO mapToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setDriverId(driver.getDriverId());
        dto.setDriverName(driver.getDriverName());
        dto.setContactNumber(driver.getContactNumber());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setCreatedAt(driver.getCreatedAt());
        dto.setUpdatedAt(driver.getUpdatedAt());
        return dto;
    }
}