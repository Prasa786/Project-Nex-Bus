<<<<<<< HEAD
package com.nexbus.nexbus_backend.controller;

import com.nexbus.nexbus_backend.dto.RouteDTO;
import com.nexbus.nexbus_backend.service.RouteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        return ResponseEntity.ok(routeService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<RouteDTO> getRouteById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        return ResponseEntity.ok(routeService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDTO> createRoute(@Valid @RequestBody RouteDTO routeDTO) {
        return ResponseEntity.status(201).body(routeService.save(routeDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDTO> updateRoute(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id,
            @Valid @RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok(routeService.update(id, routeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoute(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        routeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
=======
package com.nexbus.nexbus_backend.controller;

import com.nexbus.nexbus_backend.dto.RouteDTO;
import com.nexbus.nexbus_backend.service.RouteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        return ResponseEntity.ok(routeService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<RouteDTO> getRouteById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        return ResponseEntity.ok(routeService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDTO> createRoute(@Valid @RequestBody RouteDTO routeDTO) {
        return ResponseEntity.status(201).body(routeService.save(routeDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDTO> updateRoute(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id,
            @Valid @RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok(routeService.update(id, routeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoute(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        routeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}