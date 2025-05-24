<<<<<<< HEAD
package com.nexbus.nexbus_backend.controller;

import com.nexbus.nexbus_backend.dto.BusDTO;
import com.nexbus.nexbus_backend.dto.OperatorDTO;
import com.nexbus.nexbus_backend.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bus-operators")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OperatorDTO> create(@RequestBody OperatorDTO dto) {
        OperatorDTO saved = operatorService.save(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<OperatorDTO>> getAll() {
        return ResponseEntity.ok(operatorService.findAll());
    }

    @GetMapping("/{operatorId}/buses")
    @PreAuthorize("hasAnyAuthority('BUSOPERATOR', 'ADMIN')")
    public ResponseEntity<List<BusDTO>> getBusesByOperatorId(
            @PathVariable Integer operatorId,
            Authentication authentication) {
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        Integer userId = operatorService.findById(operatorId).getUserId();
        List<BusDTO> buses = operatorService.findBusesByOperatorId(operatorId, userId, isAdmin);
        return ResponseEntity.ok(buses);
    }
=======
package com.nexbus.nexbus_backend.controller;

import com.nexbus.nexbus_backend.dto.OperatorDTO;
import com.nexbus.nexbus_backend.service.OperatorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operators")
public class OperatorController {

    private final OperatorService operatorService;

    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<List<OperatorDTO>> getAllOperators() {
        return ResponseEntity.ok(operatorService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @operatorService.isOperatorOwner(#id, authentication.principal.userId))")
    public ResponseEntity<OperatorDTO> getOperatorById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        return ResponseEntity.ok(operatorService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OperatorDTO> createOperator(@Valid @RequestBody OperatorDTO operatorDTO) {
        return ResponseEntity.status(201).body(operatorService.save(operatorDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OperatorDTO> updateOperator(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id,
            @Valid @RequestBody OperatorDTO operatorDTO) {
        return ResponseEntity.ok(operatorService.update(id, operatorDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOperator(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        operatorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}