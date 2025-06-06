package com.nexbus.nexbus_backend.service;

import com.nexbus.nexbus_backend.dto.BookingDTO;
import com.nexbus.nexbus_backend.dto.UserDTO;
import com.nexbus.nexbus_backend.exception.MailSendingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JavaMailSender mailSender;
    private final UserService userService;
    private final String fromEmail;
    private final String templatePath;
    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    public EmailService(JavaMailSender mailSender, UserService userService, 
                       @Value("${spring.mail.username}") String fromEmail,
                       @Value("${email.template.path:templates/}") String templatePath) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.fromEmail = fromEmail;
        this.templatePath = templatePath;
    }

    public void sendBookingConfirmationEmail(BookingDTO bookingDTO) {
        try {
            validateBookingDTO(bookingDTO);
            UserDTO user = validateUser(bookingDTO.getUserId());
            String template = loadTemplate("BookingEmailTemplate.html");
            String emailContent = populateBookingTemplate(template, bookingDTO, user);
            sendEmail(user.getEmail(), "Booking Confirmation - NexBus", emailContent, true);
            logger.info("Sent booking confirmation email for booking ID: {}", bookingDTO.getBookingId());
        } catch (IOException | MessagingException e) {
            logger.error("Failed to send booking confirmation email for booking ID: {}", 
                         bookingDTO.getBookingId(), e);
            throw new MailSendingException("Failed to send booking confirmation email for booking ID: " + 
                                           bookingDTO.getBookingId(), e);
        }
    }

    public void sendCancellationEmail(BookingDTO bookingDTO) {
        try {
            validateBookingDTO(bookingDTO);
            UserDTO user = validateUser(bookingDTO.getUserId());
            String template = loadTemplate("CancellationEmailTemplate.html");
            String emailContent = populateBookingTemplate(template, bookingDTO, user);
            sendEmail(user.getEmail(), "Booking Cancellation - NexBus", emailContent, true);
            logger.info("Sent cancellation email for booking ID: {}", bookingDTO.getBookingId());
        } catch (IOException | MessagingException e) {
            logger.error("Failed to send cancellation email for booking ID: {}", 
                         bookingDTO.getBookingId(), e);
            throw new MailSendingException("Failed to send cancellation email for booking ID: " + 
                                           bookingDTO.getBookingId(), e);
        }
    }

    public void sendRefundEmail(BookingDTO bookingDTO) {
        try {
            validateBookingDTO(bookingDTO);
            UserDTO user = validateUser(bookingDTO.getUserId());
            String template = loadTemplate("RefundEmailTemplate.html");
            String emailContent = populateBookingTemplate(template, bookingDTO, user);
            sendEmail(user.getEmail(), "Refund Confirmation - NexBus", emailContent, true);
            logger.info("Sent refund email for booking ID: {}", bookingDTO.getBookingId());
        } catch (IOException | MessagingException e) {
            logger.error("Failed to send refund email for booking ID: {}", 
                         bookingDTO.getBookingId(), e);
            throw new MailSendingException("Failed to send refund email for booking ID: " + 
                                           bookingDTO.getBookingId(), e);
        }
    }

    private void validateBookingDTO(BookingDTO bookingDTO) {
        if (bookingDTO == null || bookingDTO.getUserId() == null || bookingDTO.getBookingId() == null ||
            bookingDTO.getBookingDate() == null || bookingDTO.getStatus() == null || bookingDTO.getFare() == null) {
            throw new IllegalArgumentException("Invalid booking data provided");
        }
    }

    private UserDTO validateUser(Integer userId) {
        UserDTO user = userService.findById(userId);
        if (user == null || user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid or missing user email for user ID: " + userId);
        }
        return user;
    }

    private String loadTemplate(String templateName) throws IOException {
        return templateCache.computeIfAbsent(templateName, name -> {
            try {
                ClassPathResource resource = new ClassPathResource(templatePath + name);
                return Files.readString(Paths.get(resource.getURI()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load template: " + name, e);
            }
        });
    }

    private String populateBookingTemplate(String template, BookingDTO bookingDTO, UserDTO user) {
        return template
            .replace("{userName}", user.getFirstName() + " " + user.getLastName())
            .replace("{bookingId}", bookingDTO.getBookingId().toString())
            .replace("{busId}", bookingDTO.getBusId().toString())
            .replace("{seatId}", bookingDTO.getSeatId().toString())
            .replace("{scheduleId}", bookingDTO.getScheduleId().toString())
            .replace("{bookingDate}", bookingDTO.getBookingDate().format(DATE_FORMATTER))
            .replace("{status}", bookingDTO.getStatus())
            .replace("{fare}", bookingDTO.getFare().toString());
    }

    private void sendEmail(String to, String subject, String content, boolean isHtml) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, isHtml);
        
        mailSender.send(message);
    }
}