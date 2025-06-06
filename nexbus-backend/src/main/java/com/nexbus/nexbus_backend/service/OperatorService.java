package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.AmenitiesDTO;
import com.nexbus.nexbus_backend.dto.BusDTO;
import com.nexbus.nexbus_backend.dto.DriverDTO;
import com.nexbus.nexbus_backend.dto.MaintenanceRecordDTO;
import com.nexbus.nexbus_backend.dto.OperatorDTO;
import com.nexbus.nexbus_backend.dto.ScheduleDTO;
import com.nexbus.nexbus_backend.dto.RouteDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Amenity;
import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.BusOperator;
import com.nexbus.nexbus_backend.model.Driver;
import com.nexbus.nexbus_backend.model.MaintenanceRecord;
import com.nexbus.nexbus_backend.model.Role.RoleName;
import com.nexbus.nexbus_backend.model.Route;
import com.nexbus.nexbus_backend.model.Schedule;
import com.nexbus.nexbus_backend.model.User;
import com.nexbus.nexbus_backend.repository.BusOperatorRepository;
import com.nexbus.nexbus_backend.repository.BusRepository;
import com.nexbus.nexbus_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperatorService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorService.class);

    private final BusOperatorRepository busOperatorRepository;
    private final BusRepository busRepository;
    private final UserRepository userRepository;

    public OperatorService(BusOperatorRepository busOperatorRepository, BusRepository busRepository, UserRepository userRepository) {
        this.busOperatorRepository = busOperatorRepository;
        this.busRepository = busRepository;
        this.userRepository = userRepository;
    }

    public List<OperatorDTO> findAll() {
        logger.debug("Fetching all operators");
        List<OperatorDTO> operators = busOperatorRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} operators", operators.size());
        return operators;
    }

    public OperatorDTO findById(Integer id) {
        logger.debug("Fetching operator with ID: {}", id);
        BusOperator operator = busOperatorRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", id);
                return new ResourceNotFoundException("Operator", "ID", id);
            });
        logger.info("Found operator with ID: {}", id);
        return mapToDTO(operator);
    }

    @Transactional
    public OperatorDTO save(OperatorDTO operatorDTO) {
        logger.debug("Saving new operator: {}", operatorDTO.getOperatorName());
        if (operatorDTO.getUserId() == null) {
            logger.error("User ID must be provided for operator: {}", operatorDTO.getOperatorName());
            throw new IllegalArgumentException("User ID must be provided");
        }
        BusOperator existingOperator = busOperatorRepository.findByEmail(operatorDTO.getEmail());
        if (existingOperator != null) {
            logger.error("Email {} is already in use by operator ID: {}", operatorDTO.getEmail(), existingOperator.getOperatorId());
            throw new IllegalStateException("Email " + operatorDTO.getEmail() + " is already in use");
        }
        BusOperator operator = mapToEntity(operatorDTO);
        operator = busOperatorRepository.save(operator);
        logger.info("Saved operator with ID: {}", operator.getOperatorId());
        return mapToDTO(operator);
    }

    @Transactional
    public OperatorDTO update(Integer id, OperatorDTO operatorDTO) {
        logger.debug("Updating operator with ID: {}", id);
        BusOperator existingOperator = busOperatorRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", id);
                return new ResourceNotFoundException("Operator", "ID", id);
            });
        if (operatorDTO.getUserId() == null) {
            logger.error("User ID must be provided for operator: {}", operatorDTO.getOperatorName());
            throw new IllegalArgumentException("User ID must be provided");
        }
        if (!existingOperator.getEmail().equals(operatorDTO.getEmail())) {
            BusOperator operatorWithEmail = busOperatorRepository.findByEmail(operatorDTO.getEmail());
            if (operatorWithEmail != null) {
                logger.error("Email {} is already in use by operator ID: {}", operatorDTO.getEmail(), operatorWithEmail.getOperatorId());
                throw new IllegalStateException("Email " + operatorDTO.getEmail() + " is already in use");
            }
        }
        existingOperator.setOperatorName(operatorDTO.getOperatorName());
        existingOperator.setContactNumber(operatorDTO.getContactNumber());
        existingOperator.setEmail(operatorDTO.getEmail());
        existingOperator.setAddress(operatorDTO.getAddress());
        User user = userRepository.findById(operatorDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "ID", operatorDTO.getUserId()));
        existingOperator.setUser(user);
        busOperatorRepository.save(existingOperator);
        logger.info("Updated operator with ID: {}", id);
        return mapToDTO(existingOperator);
    }

    @Transactional
    public void deleteById(Integer id) {
        logger.debug("Deleting operator with ID: {}", id);
        BusOperator operator = busOperatorRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", id);
                return new ResourceNotFoundException("Operator", "ID", id);
            });
        long busCount = busRepository.countByOperatorOperatorId(id);
        if (busCount > 0) {
            logger.error("Cannot delete operator ID: {} because it has {} associated bus(es)", id, busCount);
            throw new IllegalStateException("Cannot delete operator with ID " + id + " because it has " + busCount + " associated bus(es)");
        }
        busOperatorRepository.deleteById(id);
        logger.info("Deleted operator with ID: {}", id);
    }

    public boolean isOperatorOwner(Integer operatorId, Integer userId) {
        logger.debug("Checking if user ID: {} owns operator ID: {}", userId, operatorId);
        return busOperatorRepository.findById(operatorId)
            .map(operator -> {
                boolean isOwner = operator.getUser().getUserId().equals(userId);
                logger.debug("User ID: {} is {} the owner of operator ID: {}", userId, isOwner ? "" : "not", operatorId);
                return isOwner;
            })
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {} while checking ownership for user ID: {}", operatorId, userId);
                return new ResourceNotFoundException("Operator", "ID", operatorId);
            });
    }

    public boolean hasBusWithOperatorRoleName(Integer busId, RoleName roleName) {
        logger.debug("Checking if bus ID: {} is associated with operator role: {}", busId, roleName);
        boolean exists = busRepository.existsByBusIdAndOperatorUserRoleName(busId, roleName.name());
        logger.info("Bus ID: {} {} associated with operator role: {}", busId, exists ? "is" : "is not", roleName);
        return exists;
    }

    @Transactional(readOnly = true)
    public List<BusDTO> findBusesByOperatorId(Integer operatorId, Integer userId, boolean isAdmin) {
        logger.debug("Fetching buses for operator ID: {} by user ID: {}", operatorId, userId);
        BusOperator operator = busOperatorRepository.findById(operatorId)
            .orElseThrow(() -> {
                logger.error("Operator not found with ID: {}", operatorId);
                return new ResourceNotFoundException("Operator", "ID", operatorId);
            });

        // Security check: Ensure user is the operator owner or an admin
        if (!isAdmin && !operator.getUser().getUserId().equals(userId)) {
            logger.error("User ID: {} is not authorized to access buses for operator ID: {}", userId, operatorId);
            throw new SecurityException("Unauthorized access to operator's buses");
        }

        List<Bus> buses = busRepository.findByOperator_OperatorId(operatorId);
        List<BusDTO> busDTOs = buses.stream().map(this::mapBusToDTO).collect(Collectors.toList());
        logger.info("Retrieved {} buses for operator ID: {}", busDTOs.size(), operatorId);
        return busDTOs;
    }

    private OperatorDTO mapToDTO(BusOperator operator) {
        OperatorDTO dto = new OperatorDTO();
        dto.setOperatorId(operator.getOperatorId());
        dto.setOperatorName(operator.getOperatorName());
        dto.setContactNumber(operator.getContactNumber());
        dto.setEmail(operator.getEmail());
        dto.setAddress(operator.getAddress());
        dto.setUserId(operator.getUser().getUserId());
        dto.setCreatedAt(operator.getCreatedAt());
        dto.setUpdatedAt(operator.getUpdatedAt());
        return dto;
    }

    private BusOperator mapToEntity(OperatorDTO dto) {
        BusOperator operator = new BusOperator();
        operator.setOperatorId(dto.getOperatorId());
        operator.setOperatorName(dto.getOperatorName());
        operator.setContactNumber(dto.getContactNumber());
        operator.setEmail(dto.getEmail());
        operator.setAddress(dto.getAddress());
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "ID", dto.getUserId()));
        operator.setUser(user);
        return operator;
    }

    private BusDTO mapBusToDTO(Bus bus) {
        BusDTO dto = new BusDTO();
        dto.setBusId(bus.getBusId());
        dto.setOperatorId(bus.getOperator().getOperatorId());
        dto.setRouteId(bus.getRoute() != null ? bus.getRoute().getRouteId() : null);
        dto.setBusNumber(bus.getBusNumber());
        dto.setTotalSeats(bus.getTotalSeats());
        dto.setAmenities(bus.getAmenities().stream().map(this::mapAmenityToDTO).collect(Collectors.toList()));
        dto.setMaintenanceRecords(bus.getMaintenanceRecords().stream().map(this::mapMaintenanceRecordToDTO).collect(Collectors.toList()));
        dto.setDriver(bus.getDriver() != null ? mapDriverToDTO(bus.getDriver()) : null);
        dto.setSchedules(bus.getSchedules().stream().map(this::mapScheduleToDTO).collect(Collectors.toList()));
        dto.setCreatedAt(bus.getCreatedAt());
        dto.setUpdatedAt(bus.getUpdatedAt());
        return dto;
    }

    private AmenitiesDTO mapAmenityToDTO(Amenity amenity) {
        AmenitiesDTO dto = new AmenitiesDTO();
        dto.setAmenityId(amenity.getAmenityId());
        dto.setAmenityName(amenity.getAmenityName()); // Updated to match AmenitiesDTO
        dto.setDescription(amenity.getDescription());
        dto.setCreatedAt(amenity.getCreatedAt());
        dto.setUpdatedAt(amenity.getUpdatedAt());
        return dto;
    }

    private MaintenanceRecordDTO mapMaintenanceRecordToDTO(MaintenanceRecord record) {
        MaintenanceRecordDTO dto = new MaintenanceRecordDTO();
        dto.setMaintenanceId(record.getMaintenanceId());
        dto.setDescription(record.getDescription());
        dto.setMaintenanceDate(record.getMaintenanceDate());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }

    private DriverDTO mapDriverToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setDriverId(driver.getDriverId());
        dto.setDriverName(driver.getDriverName()); // Updated to match DriverDTO
        dto.setContactNumber(driver.getContactNumber());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setCreatedAt(driver.getCreatedAt());
        dto.setUpdatedAt(driver.getUpdatedAt());
        return dto;
    }

    private ScheduleDTO mapScheduleToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setDepartureTime(schedule.getDepartureTime());
        dto.setArrivalTime(schedule.getArrivalTime());
        dto.setCreatedAt(schedule.getCreatedAt());
        dto.setUpdatedAt(schedule.getUpdatedAt());
        return dto;
    }
}