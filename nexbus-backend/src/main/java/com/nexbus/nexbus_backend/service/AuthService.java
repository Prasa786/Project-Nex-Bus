package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.LoginRequest;
import com.nexbus.nexbus_backend.dto.LoginResponse;
import com.nexbus.nexbus_backend.dto.RegisterRequest;
import com.nexbus.nexbus_backend.dto.UserDTO;
import com.nexbus.nexbus_backend.exception.UserAlreadyExistsException;
import com.nexbus.nexbus_backend.model.Role;
import com.nexbus.nexbus_backend.model.User;
import com.nexbus.nexbus_backend.repository.RoleRepository;
import com.nexbus.nexbus_backend.repository.UserRepository;
import com.nexbus.nexbus_backend.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    public UserDTO register(RegisterRequest registerRequest) {
        logger.debug("Registering user with email: {}", registerRequest.getEmail());
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            logger.error("Email already exists: {}", registerRequest.getEmail());
            throw new UserAlreadyExistsException("Email already exists: " + registerRequest.getEmail());
        }

        // Determine the role
        Role.RoleName roleNameEnum;
        if (registerRequest.getRoleName() != null && !registerRequest.getRoleName().isEmpty()) {
            try {
                roleNameEnum = Role.RoleName.valueOf(registerRequest.getRoleName().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid role name: {}", registerRequest.getRoleName());
                throw new IllegalArgumentException("Invalid role name: " + registerRequest.getRoleName());
            }
        } else {
            roleNameEnum = Role.RoleName.CUSTOMER; // Default role
        }

        // For CUSTOMER, enforce all fields
        if (roleNameEnum == Role.RoleName.CUSTOMER) {
            if (registerRequest.getFirstName() == null || registerRequest.getFirstName().trim().isEmpty()) {
                logger.error("First name is required for CUSTOMER role");
                throw new IllegalArgumentException("First name is required for CUSTOMER role");
            }
            if (registerRequest.getLastName() == null || registerRequest.getLastName().trim().isEmpty()) {
                logger.error("Last name is required for CUSTOMER role");
                throw new IllegalArgumentException("Last name is required for CUSTOMER role");
            }
            if (registerRequest.getPhoneNumber() == null || registerRequest.getPhoneNumber().trim().isEmpty()) {
                logger.error("Phone number is required for CUSTOMER role");
                throw new IllegalArgumentException("Phone number is required for CUSTOMER role");
            }
            if (registerRequest.getAddress() == null || registerRequest.getAddress().trim().isEmpty()) {
                logger.error("Address is required for CUSTOMER role");
                throw new IllegalArgumentException("Address is required for CUSTOMER role");
            }
        }

        Role role = roleRepository.findByRoleName(roleNameEnum)
                .orElseGet(() -> {
                    Role newRole = new Role(roleNameEnum);
                    return roleRepository.save(newRole);
                });

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setRole(role);
        user = userRepository.save(user);
        logger.info("User registered successfully: {}", registerRequest.getEmail());

        return mapToUserDTO(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        logger.debug("Attempting login for email: {}", loginRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            logger.info("Authentication successful for email: {}", loginRequest.getEmail());

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .orElse("CUSTOMER");

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setEmail(userDetails.getUsername());
            response.setRole(role);
            logger.info("Login successful for email: {} with role: {}", loginRequest.getEmail(), role);
            return response;
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for email: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Invalid email or password", e);
        } catch (Exception e) {
            logger.error("Login failed for email: {}. Error: {}", loginRequest.getEmail(), e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
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

    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}