package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.PassengerDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.Role;
import com.nexbus.nexbus_backend.model.Role.RoleName;
import com.nexbus.nexbus_backend.model.User;
import com.nexbus.nexbus_backend.repository.RoleRepository;
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
public class PassengerService {

    private static final Logger logger = LoggerFactory.getLogger(PassengerService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public PassengerService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<PassengerDTO> findAll() {
        logger.debug("Fetching all passengers");
        List<PassengerDTO> passengers = userRepository.findAll().stream()
            .filter(u -> RoleName.CUSTOMER.equals(u.getRole().getRoleName()))
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} passengers", passengers.size());
        return passengers;
    }

    public PassengerDTO findById(Integer id) {
        logger.debug("Fetching passenger with ID: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Passenger not found with ID: {}", id);
                return new ResourceNotFoundException("Passenger", "ID", id);
            });
        if (!RoleName.CUSTOMER.equals(user.getRole().getRoleName())) {
            logger.error("User with ID: {} is not a CUSTOMER", id);
            throw new ResourceNotFoundException("Passenger", "ID", id);
        }
        logger.info("Found passenger with ID: {}", id);
        return mapToDTO(user);
    }

    public PassengerDTO save(PassengerDTO passengerDTO) {
        logger.debug("Saving new passenger with email: {}", passengerDTO.getEmail());
        if (passengerDTO.getFirstName() == null || passengerDTO.getFirstName().trim().isEmpty()) {
            logger.error("First name is required for CUSTOMER");
            throw new IllegalArgumentException("First name is required for CUSTOMER");
        }
        if (passengerDTO.getLastName() == null || passengerDTO.getLastName().trim().isEmpty()) {
            logger.error("Last name is required for CUSTOMER");
            throw new IllegalArgumentException("Last name is required for CUSTOMER");
        }
        if (passengerDTO.getPhoneNumber() == null || passengerDTO.getPhoneNumber().trim().isEmpty()) {
            logger.error("Phone number is required for CUSTOMER");
            throw new IllegalArgumentException("Phone number is required for CUSTOMER");
        }
        if (passengerDTO.getAddress() == null || passengerDTO.getAddress().trim().isEmpty()) {
            logger.error("Address is required for CUSTOMER");
            throw new IllegalArgumentException("Address is required for CUSTOMER");
        }

        Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER)
            .orElseGet(() -> {
                Role newRole = new Role(RoleName.CUSTOMER);
                return roleRepository.save(newRole);
            });

        User user = mapToEntity(passengerDTO);
        user.setRole(customerRole);
        user = userRepository.save(user);
        logger.info("Passenger saved successfully with ID: {}", user.getUserId());
        return mapToDTO(user);
    }

    public PassengerDTO update(Integer id, PassengerDTO passengerDTO) {
        logger.debug("Updating passenger with ID: {}", id);
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Passenger not found with ID: {}", id);
                return new ResourceNotFoundException("Passenger", "ID", id);
            });
        if (!RoleName.CUSTOMER.equals(existingUser.getRole().getRoleName())) {
            logger.error("User with ID: {} is not a CUSTOMER", id);
            throw new ResourceNotFoundException("Passenger", "ID", id);
        }
        existingUser.setEmail(passengerDTO.getEmail());
        existingUser.setFirstName(passengerDTO.getFirstName());
        existingUser.setLastName(passengerDTO.getLastName());
        existingUser.setPhoneNumber(passengerDTO.getPhoneNumber());
        existingUser.setAddress(passengerDTO.getAddress());
        userRepository.save(existingUser);
        logger.info("Passenger updated successfully with ID: {}", id);
        return mapToDTO(existingUser);
    }

    public void deleteById(Integer id) {
        logger.debug("Deleting passenger with ID: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Passenger not found with ID: {}", id);
                return new ResourceNotFoundException("Passenger", "ID", id);
            });
        if (!RoleName.CUSTOMER.equals(user.getRole().getRoleName())) {
            logger.error("User with ID: {} is not a CUSTOMER", id);
            throw new ResourceNotFoundException("Passenger", "ID", id);
        }
        userRepository.deleteById(id);
        logger.info("Passenger deleted successfully with ID: {}", id);
    }

    private PassengerDTO mapToDTO(User user) {
        PassengerDTO dto = new PassengerDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getEmail());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole().getRoleName().name());
        dto.setCreatedAt(convertToLocalDateTime(user.getCreatedAt()));
        dto.setUpdatedAt(convertToLocalDateTime(user.getUpdatedAt()));
        return dto;
    }

    private User mapToEntity(PassengerDTO dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        return user;
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}