package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.PaymentDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.*;
import com.nexbus.nexbus_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BusOperatorRepository busOperatorRepository;
    private final BusRepository busRepository;

    public PaymentService(PaymentRepository paymentRepository,
                         BusOperatorRepository busOperatorRepository,
                         BusRepository busRepository) {
        this.paymentRepository = paymentRepository;
        this.busOperatorRepository = busOperatorRepository;
        this.busRepository = busRepository;
    }

    // Regular payment processing
    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        validatePayment(paymentDTO);
        Payment payment = buildAndSavePayment(paymentDTO);
        return mapToDTO(payment);
    }

    // Specific bus rent payment processing
    @Transactional
    public PaymentDTO processBusRentPayment(PaymentDTO paymentDTO) {
        // Additional validation for bus rent payments
        if (paymentDTO.getBusId() == null) {
            throw new IllegalArgumentException("Bus ID is required for rent payments");
        }
        if (!"RENT".equalsIgnoreCase(paymentDTO.getPaymentType())) {
            paymentDTO.setPaymentType("RENT");
        }
        
        validatePayment(paymentDTO);
        Payment payment = buildAndSavePayment(paymentDTO);
        return mapToDTO(payment);
    }

    // Refund processing
    @Transactional
    public PaymentDTO processRefundPayment(PaymentDTO paymentDTO) {
        // Refunds must be negative amounts
        if (paymentDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            paymentDTO.setAmount(paymentDTO.getAmount().negate());
        }
        paymentDTO.setPaymentType("REFUND");
        
        validatePayment(paymentDTO);
        Payment payment = buildAndSavePayment(paymentDTO);
        return mapToDTO(payment);
    }

    // Common validation logic
    private void validatePayment(PaymentDTO paymentDTO) {
        if (paymentDTO.getOperatorId() == null) {
            throw new IllegalArgumentException("Operator ID is required");
        }
        if (paymentDTO.getAmount() == null || paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (paymentDTO.getPaymentType() == null || paymentDTO.getPaymentType().isEmpty()) {
            throw new IllegalArgumentException("Payment type is required");
        }
    }

    // Common payment creation logic
    private Payment buildAndSavePayment(PaymentDTO paymentDTO) {
        // Validate operator exists
        if (!busOperatorRepository.existsById(paymentDTO.getOperatorId())) {
            throw new ResourceNotFoundException("Operator", "ID", paymentDTO.getOperatorId());
        }

        // Validate bus exists and belongs to operator if specified
        Bus bus = null;
        if (paymentDTO.getBusId() != null) {
            bus = busRepository.findById(paymentDTO.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "ID", paymentDTO.getBusId()));
            
            if (!bus.getOperator().getOperatorId().equals(paymentDTO.getOperatorId())) {
                throw new IllegalStateException("Bus does not belong to the specified operator");
            }
        }

        Payment payment = new Payment();
        payment.setOperatorId(paymentDTO.getOperatorId());
        payment.setBus(bus);
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentType(paymentDTO.getPaymentType());
        payment.setPaymentDate(paymentDTO.getPaymentDate() != null ? 
            paymentDTO.getPaymentDate() : LocalDateTime.now());
        payment.setDescription(paymentDTO.getDescription());

        return paymentRepository.save(payment);
    }

    // Query methods
    public List<PaymentDTO> findAll() {
        return paymentRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public PaymentDTO findById(Integer id) {
        return paymentRepository.findById(id)
            .map(this::mapToDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Payment", "ID", id));
    }

    public List<PaymentDTO> findByOperatorId(Integer operatorId) {
        return paymentRepository.findByOperatorId(operatorId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public List<PaymentDTO> findByBusId(Integer busId) {
        return paymentRepository.findByBus_BusId(busId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    private PaymentDTO mapToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setOperatorId(payment.getOperatorId());
        dto.setBusId(payment.getBus() != null ? payment.getBus().getBusId() : null);
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setDescription(payment.getDescription());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
}