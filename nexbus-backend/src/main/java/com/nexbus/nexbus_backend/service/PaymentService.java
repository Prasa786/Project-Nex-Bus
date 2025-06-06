package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.PaymentDTO;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
import com.nexbus.nexbus_backend.model.*;
import com.nexbus.nexbus_backend.repository.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BusOperatorRepository busOperatorRepository;
    private final BusRepository busRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public PaymentService(PaymentRepository paymentRepository,
                         BusOperatorRepository busOperatorRepository,
                         BusRepository busRepository) {
        this.paymentRepository = paymentRepository;
        this.busOperatorRepository = busOperatorRepository;
        this.busRepository = busRepository;
    }

     // Create Razorpay order
     public Order createRazorpayOrder(PaymentDTO paymentDTO) throws Exception {
         if (paymentDTO.getAmount() == null || paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
             throw new IllegalArgumentException("Amount must be positive");
         }
         RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

         Map<String, Object> options = new HashMap<>();
         options.put("amount", paymentDTO.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
         options.put("currency", "INR");
         options.put("receipt", String.valueOf(paymentDTO.getPaymentId()));
         options.put("payment_capture", 1);

         return razorpayClient.orders.create((JSONObject) options);
     }

     // Process payment after Razorpay payment is confirmed
     @Transactional
     public PaymentDTO processPayment(String paymentId, String orderId, PaymentDTO paymentDTO) {
         validatePayment(paymentDTO);

         Payment payment = buildAndSavePayment(paymentDTO);
         payment.setRazorpayPaymentId(paymentId);
         return mapToDTO(payment);
     }

     private void validatePayment(PaymentDTO paymentDTO) {
         if (paymentDTO.getAmount() == null || paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
             throw new IllegalArgumentException("Invalid payment amount");
         }
         if (paymentDTO.getOperatorId() == null) {
             throw new IllegalArgumentException("Operator ID is required");
         }
     }

     private Payment buildAndSavePayment(PaymentDTO paymentDTO) {
         if (!busOperatorRepository.existsById(paymentDTO.getOperatorId())) {
             throw new ResourceNotFoundException("Operator", "ID", paymentDTO.getOperatorId());
         }

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

     private PaymentDTO mapToDTO(Payment payment) {
         PaymentDTO dto = new PaymentDTO();
         dto.setPaymentId(payment.getPaymentId());
         dto.setOperatorId(payment.getOperatorId());
         if (payment.getBus() != null) {
             dto.setBusId(payment.getBus().getBusId());
         }
         dto.setAmount(payment.getAmount());
         dto.setPaymentType(payment.getPaymentType());
         dto.setPaymentDate(payment.getPaymentDate());
         dto.setDescription(payment.getDescription());
         dto.setRazorpayPaymentId(payment.getRazorpayPaymentId());
         return dto;
     }
 }
