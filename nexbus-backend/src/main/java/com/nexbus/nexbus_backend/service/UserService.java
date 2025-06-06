package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.PassengerDTO;
import com.nexbus.nexbus_backend.dto.UserDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Role;
import com.nexbus.nexbus_backend.model.User;
import com.nexbus.nexbus_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findAll() {
        logger.debug("Fetching all users");
        List<UserDTO> users = userRepository.findAll().stream()
            .map(this::mapToUserDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} users", users.size());
        return users;
    }

    public UserDTO findById(Integer userId) { // Changed String to Integer
        logger.debug("Fetching user with userId: {}", userId);
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> {
                logger.error("User not found with userId: {}", userId);
                return new ResourceNotFoundException("User", "userId", userId);
            });
        logger.info("Found user with userId: {}", userId);
        return mapToUserDTO(user);
    }

    public List<PassengerDTO> findAllPassengers() {
        logger.debug("Fetching all passengers (CUSTOMER role)");
        List<PassengerDTO> passengers = userRepository.findAll().stream()
            .filter(user -> user.getRole().getRoleName() == Role.RoleName.CUSTOMER)
            .map(this::mapToPassengerDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} passengers", passengers.size());
        return passengers;
    }

    public PassengerDTO findPassengerById(Integer id) {
        logger.debug("Fetching passenger with ID: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", id);
                return new ResourceNotFoundException("User", "ID", id);
            });
        if (user.getRole().getRoleName() != Role.RoleName.CUSTOMER) {
            logger.error("User with ID: {} is not a CUSTOMER", id);
            throw new IllegalStateException("User with ID: " + id + " is not a CUSTOMER");
        }
        logger.info("Found passenger with ID: {}", id);
        return mapToPassengerDTO(user);
    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setRoleName(user.getRole().getRoleName().toString());
        dto.setCreatedAt(convertToLocalDateTime(user.getCreatedAt()));
        dto.setUpdatedAt(convertToLocalDateTime(user.getUpdatedAt()));
        return dto;
    }

    private PassengerDTO mapToPassengerDTO(User user) {
        PassengerDTO dto = new PassengerDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getEmail());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setCreatedAt(convertToLocalDateTime(user.getCreatedAt()));
        dto.setUpdatedAt(convertToLocalDateTime(user.getUpdatedAt()));
        return dto;
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}