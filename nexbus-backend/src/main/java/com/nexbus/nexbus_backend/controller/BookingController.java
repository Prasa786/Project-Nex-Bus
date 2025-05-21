package com.nexbus.nexbus_backend.controller;

import com.nexbus.nexbus_backend.dto.BookingDTO;
import com.nexbus.nexbus_backend.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAll());
    }

    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#busId, authentication.principal.userId))")
    public ResponseEntity<List<BookingDTO>> getBookingsByBusId(
            @PathVariable @Min(value = 1, message = "Bus ID must be positive") Integer busId) {
        return ResponseEntity.ok(bookingService.findByBusId(busId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @bookingService.isBookingOwner(#id, authentication.principal.userId)) or (hasRole('BUSOPERATOR') and @bookingService.isBookingOperator(#id, authentication.principal.userId))")
    public ResponseEntity<BookingDTO> getBookingById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        return ResponseEntity.ok(bookingService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.status(201).body(bookingService.save(bookingDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @bookingService.isBookingOwner(#id, authentication.principal.userId)) or (hasRole('BUSOPERATOR') and @bookingService.isBookingOperator(#id, authentication.principal.userId))")
    public ResponseEntity<BookingDTO> updateBooking(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id,
            @Valid @RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.update(id, bookingDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @bookingService.isBookingOwner(#id, authentication.principal.userId)) or (hasRole('BUSOPERATOR') and @bookingService.isBookingOperator(#id, authentication.principal.userId))")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        bookingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @bookingService.isBookingOwner(#id, authentication.principal.userId)) or (hasRole('BUSOPERATOR') and @bookingService.isBookingOperator(#id, authentication.principal.userId))")
    public ResponseEntity<BigDecimal> processRefund(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = extractUserId(userDetails);
        return ResponseEntity.ok(bookingService.processRefund(id, userId));
    }

    private Integer extractUserId(UserDetails userDetails) {
        // Assuming UserDetails.getUsername() returns the user ID as a string
        try {
            return Integer.parseInt(userDetails.getUsername());
        } catch (NumberFormatException e) {
            logger.error("Invalid user ID format in UserDetails username: {}", userDetails.getUsername());
            throw new IllegalStateException("User ID must be a valid integer");
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
}