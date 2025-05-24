<<<<<<< HEAD
package com.nexbus.nexbus_backend.controller;

import com.nexbus.nexbus_backend.dto.PaymentDTO;
import com.nexbus.nexbus_backend.service.BusService;
import com.nexbus.nexbus_backend.service.OperatorService;
import com.nexbus.nexbus_backend.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final OperatorService operatorService;
    private final BusService busService;

    public PaymentController(PaymentService paymentService, OperatorService operatorService, BusService busService) {
        this.paymentService = paymentService;
        this.operatorService = operatorService;
        this.busService = busService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<PaymentDTO> getPaymentById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @GetMapping("/operator/{operatorId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @operatorService.isOperatorOwner(#operatorId, authentication.principal.userId))")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOperatorId(
            @PathVariable @Min(value = 1, message = "Operator ID must be positive") Integer operatorId) {
        return ResponseEntity.ok(paymentService.findByOperatorId(operatorId));
    }

    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#busId, authentication.principal.userId))")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByBusId(
            @PathVariable @Min(value = 1, message = "Bus ID must be positive") Integer busId) {
        return ResponseEntity.ok(paymentService.findByBusId(busId));
    }

    @PostMapping("/bus-rent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDTO> processBusRentPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.status(201).body(paymentService.processBusRentPayment(paymentDTO));
    }
=======
package com.nexbus.nexbus_backend.controller;

import com.nexbus.nexbus_backend.dto.PaymentDTO;
import com.nexbus.nexbus_backend.service.BusService;
import com.nexbus.nexbus_backend.service.OperatorService;
import com.nexbus.nexbus_backend.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final OperatorService operatorService;
    private final BusService busService;

    public PaymentController(PaymentService paymentService, OperatorService operatorService, BusService busService) {
        this.paymentService = paymentService;
        this.operatorService = operatorService;
        this.busService = busService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUSOPERATOR')")
    public ResponseEntity<PaymentDTO> getPaymentById(
            @PathVariable @Min(value = 1, message = "ID must be positive") Integer id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @GetMapping("/operator/{operatorId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @operatorService.isOperatorOwner(#operatorId, authentication.principal.userId))")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOperatorId(
            @PathVariable @Min(value = 1, message = "Operator ID must be positive") Integer operatorId) {
        return ResponseEntity.ok(paymentService.findByOperatorId(operatorId));
    }

    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('BUSOPERATOR') and @busService.isBusOwner(#busId, authentication.principal.userId))")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByBusId(
            @PathVariable @Min(value = 1, message = "Bus ID must be positive") Integer busId) {
        return ResponseEntity.ok(paymentService.findByBusId(busId));
    }

    @PostMapping("/bus-rent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDTO> processBusRentPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.status(201).body(paymentService.processBusRentPayment(paymentDTO));
    }
>>>>>>> 44bd435102e963e84bc2fef038ba51696f12ca66
}