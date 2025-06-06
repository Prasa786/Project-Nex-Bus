package com.nexbus.nexbus_backend.config;

import com.nexbus.nexbus_backend.exception.ErrorResponse;
import com.nexbus.nexbus_backend.dto.ErrorResponseDTO;
import com.nexbus.nexbus_backend.exception.MailSendingException;
import com.nexbus.nexbus_backend.exception.PaymentProcessingException;
import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("type", "about:blank");
        errorDetails.put("title", "Forbidden");
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("detail", "Access is denied");
        errorDetails.put("instance", request.getDescription(false));
        errorDetails.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("type", "about:blank");
        errorDetails.put("title", "Bad Request");
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("detail", ex.getMessage());
        errorDetails.put("instance", request.getDescription(false));
        errorDetails.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("type", "about:blank");
        errorDetails.put("title", "Unauthorized");
        errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
        errorDetails.put("detail", "Invalid email or user not found: " + ex.getMessage());
        errorDetails.put("instance", request.getDescription(false));
        errorDetails.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
        return new ResponseEntity<>(
            new ErrorResponseDTO(
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                "The requested endpoint " + ex.getRequestURL() + " with method " + ex.getHttpMethod() + " was not found on the server.",
                request.getDescription(false)
            ),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("type", "about:blank");
        errorDetails.put("title", "Not Found");
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("detail", ex.getMessage());
        errorDetails.put("instance", request.getDescription(false));
        errorDetails.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("type", "about:blank");
        errorDetails.put("title", ex.getReason());
        errorDetails.put("status", ex.getStatusCode().value());
        errorDetails.put("detail", ex.getMessage());
        errorDetails.put("instance", request.getDescription(false));
        errorDetails.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailSendingException.class)
    public ResponseEntity<Map<String, Object>> handleMailSendingException(MailSendingException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("type", "about:blank");
        errorDetails.put("title", "Internal Server Error");
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("detail", ex.getMessage());
        errorDetails.put("instance", request.getDescription(false));
        errorDetails.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("type", "about:blank");
        errorDetails.put("title", "Internal Server Error");
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("detail", "An unexpected error occurred: " + ex.getMessage());
        errorDetails.put("instance", request.getDescription(false));
        errorDetails.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<Map<String, String>> handlePaymentException(PaymentProcessingException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}