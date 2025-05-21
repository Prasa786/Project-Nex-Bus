package com.nexbus.nexbus_backend.controller;

import com.nexbus.nexbus_backend.dto.BusAmenityRequestDTO;
import com.nexbus.nexbus_backend.dto.BusDTO;
import com.nexbus.nexbus_backend.dto.SeatDTO;
import com.nexbus.nexbus_backend.dto.AmenitiesDTO;
import com.nexbus.nexbus_backend.service.BusService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<List<BusDTO>> getAllBuses() {
        return ResponseEntity.ok(busService.findAll());
    }

    @GetMapping("/operator/{operatorId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and authentication.principal.userId == #operatorId)")
    public ResponseEntity<List<BusDTO>> getBusesByOperatorId(
            @PathVariable @Min(value = 1, message = "Operator ID must be positive") Integer operatorId) {
        return ResponseEntity.ok(busService.findByOperatorId(operatorId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#id, authentication.principal.userId))")
    public ResponseEntity<BusDTO> getBusById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        return ResponseEntity.ok(busService.findById(id));
    }

    @GetMapping("/{id}/seats")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#id, authentication.principal.userId))")
    public ResponseEntity<List<SeatDTO>> getSeatsByBusId(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        return ResponseEntity.ok(busService.findSeatsByBusId(id));
    }

    @GetMapping("/{busId}/amenities")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#busId, authentication.principal.userId))")
    public ResponseEntity<List<AmenitiesDTO>> getAmenitiesByBusId(
            @PathVariable @Min(value = 1, message = "Bus ID must be positive") Integer busId) {
        BusDTO busDTO = busService.findById(busId);
        return ResponseEntity.ok(busDTO.getAmenities());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and authentication.principal.userId == #busDTO.operatorId)")
    public ResponseEntity<BusDTO> createBus(@Valid @RequestBody BusDTO busDTO) {
        return ResponseEntity.status(201).body(busService.save(busDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#id, authentication.principal.userId))")
    public ResponseEntity<BusDTO> updateBus(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id,
            @Valid @RequestBody BusDTO busDTO) {
        return ResponseEntity.ok(busService.update(id, busDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#id, authentication.principal.userId))")
    public ResponseEntity<Void> deleteBus(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        busService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{busId}/amenities/{amenityId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#busId, authentication.principal.userId))")
    public ResponseEntity<BusDTO> addAmenityToBus(
            @PathVariable @Min(value = 1, message = "Bus ID must be positive") Integer busId,
            @PathVariable @Min(value = 1, message = "Amenity ID must be positive") Integer amenityId) {
        return ResponseEntity.ok(busService.addAmenityToBus(busId, amenityId));
    }

    @DeleteMapping("/{busId}/amenities/{amenityId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#busId, authentication.principal.userId))")
    public ResponseEntity<BusDTO> removeAmenityFromBus(
            @PathVariable @Min(value = 1, message = "Bus ID must be positive") Integer busId,
            @PathVariable @Min(value = 1, message = "Amenity ID must be positive") Integer amenityId) {
        return ResponseEntity.ok(busService.removeAmenityFromBus(busId, amenityId));
    }

    @PostMapping("/amenities")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BUSOPERATOR')")
    public ResponseEntity<BusDTO> addAmenityToBus(@Valid @RequestBody BusAmenityRequestDTO request) {
        return ResponseEntity.ok(busService.addAmenityToBus(request.getBusId(), request.getAmenityId()));
    }

    @PostMapping("/add-amenity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BUSOPERATOR')")
    public ResponseEntity<BusDTO> addAmenityToBusNew(@Valid @RequestBody BusAmenityRequestDTO request) {
        return ResponseEntity.ok(busService.addAmenityToBus(request.getBusId(), request.getAmenityId()));
    }

    @PostMapping("/{busId}/driver/{driverId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#busId, authentication.principal.userId))")
    public ResponseEntity<BusDTO> assignDriverToBus(
            @PathVariable @Min(value = 1, message = "Bus ID must be positive") Integer busId,
            @PathVariable @Min(value = 1, message = "Driver ID must be positive") Integer driverId) {
        return ResponseEntity.ok(busService.assignDriverToBus(busId, driverId));
    }
}