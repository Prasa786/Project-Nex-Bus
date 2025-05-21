package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.OperatorDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.BusOperator;
import com.nexbus.nexbus_backend.model.Role.RoleName;
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
        busOperatorRepository.findByEmail(operatorDTO.getEmail())
            .ifPresent(operator -> {
                logger.error("Email {} is already in use by operator ID: {}", operatorDTO.getEmail(), operator.getOperatorId());
                throw new IllegalStateException("Email " + operatorDTO.getEmail() + " is already in use");
            });
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
            busOperatorRepository.findByEmail(operatorDTO.getEmail())
                .ifPresent(operator -> {
                    logger.error("Email {} is already in use by operator ID: {}", operatorDTO.getEmail(), operator.getOperatorId());
                    throw new IllegalStateException("Email " + operatorDTO.getEmail() + " is already in use");
                });
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
}