package com.nexbus.nexbus_backend.controller;

import com.nexbus.nexbus_backend.dto.SeatAvailabilitySummaryDTO;
import com.nexbus.nexbus_backend.dto.SeatDTO;
import com.nexbus.nexbus_backend.service.SeatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seats") // Fixed to avoid double /api prefix
@Validated
public class SeatController {

    private static final Logger logger = LoggerFactory.getLogger(SeatController.class);

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSOPERATOR', 'CUSTOMER')")
    public ResponseEntity<List<SeatDTO>> getAllSeats() {
        logger.debug("Fetching all seats");
        List<SeatDTO> seats = seatService.findAll();
        logger.info("Retrieved {} seats", seats.size());
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSOPERATOR', 'CUSTOMER')")
    public ResponseEntity<SeatDTO> getSeatById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        logger.debug("Fetching seat with ID: {}", id);
        SeatDTO seat = seatService.findById(id);
        logger.info("Found seat with ID: {}", id);
        return ResponseEntity.ok(seat);
    }

    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSOPERATOR', 'CUSTOMER')")
    public ResponseEntity<List<SeatDTO>> getSeatsByBusId(
            @PathVariable @Min(value = 1, message = "Bus ID must be positive") Integer busId) {
        logger.debug("Fetching seats for bus ID: {}", busId);
        List<SeatDTO> seats = seatService.findByBusId(busId);
        logger.info("Retrieved {} seats for bus ID: {}", seats.size(), busId);
        return ResponseEntity.ok(seats);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<SeatDTO> createSeat(@Valid @RequestBody SeatDTO seatDTO) {
        logger.debug("Creating new seat for bus ID: {}", seatDTO.getBusId());
        SeatDTO createdSeat = seatService.save(seatDTO);
        logger.info("Created seat with ID: {}", createdSeat.getSeatId());
        return ResponseEntity.status(201).body(createdSeat);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<SeatDTO> updateSeat(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id,
            @Valid @RequestBody SeatDTO seatDTO) {
        logger.debug("Updating seat with ID: {}", id);
        SeatDTO updatedSeat = seatService.update(id, seatDTO);
        logger.info("Updated seat with ID: {}", id);
        return ResponseEntity.ok(updatedSeat);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<Void> deleteSeat(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        logger.debug("Deleting seat with ID: {}", id);
        seatService.deleteById(id);
        logger.info("Deleted seat with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/book")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public ResponseEntity<SeatDTO> bookSeat(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("Booking seat ID: {} for user: {}", id, userDetails.getUsername());
        // Assuming UserDetails has a userId field or custom implementation
        Integer userId = extractUserId(userDetails);
        seatService.markAsBooked(id, userId);
        SeatDTO bookedSeat = seatService.findById(id);
        logger.info("Seat ID: {} booked for user ID: {}", id, userId);
        return ResponseEntity.ok(bookedSeat);
    }

    private Integer extractUserId(UserDetails userDetails) {
        // Placeholder: Implement based on actual UserDetails implementation
        // For example, if UserDetails is a custom class with userId
        return Integer.parseInt(userDetails.getUsername()); // Adjust based on actual implementation
    }
    
    @GetMapping("/bus/{busId}/availability")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BUSOPERATOR', 'CUSTOMER')")
    public ResponseEntity<SeatAvailabilitySummaryDTO> getSeatAvailability(
            @PathVariable @Min(value = 1, message = "Bus ID must be positive") Integer busId) {
        logger.debug("Fetching seat availability for bus ID: {}", busId);
        SeatAvailabilitySummaryDTO summary = seatService.getSeatAvailabilitySummary(busId);
        logger.info("Seat availability retrieved for bus ID: {}", busId);
        return ResponseEntity.ok(summary);
}
}