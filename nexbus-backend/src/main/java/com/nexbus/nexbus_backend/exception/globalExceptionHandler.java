//package com.nexbus.nexbus_backend.exception;
//
//import org.springframework.http.HttpStatus
//;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestControllerAdvice
//public class globalExceptionHandler {
//
//    // Handle Resource Not Found Exception
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
//        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//    }
//
//    // Handle Illegal Argument Exception (like validation errors)
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
//        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
//    // Handle other runtime exceptions
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
//        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.");
//        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    // Handle validation errors from @Valid annotation
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//            errors.put(error.getField(), error.getDefaultMessage()));
//        
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//    
//    @ExceptionHandler(MailSendingException.class)
//    public ResponseEntity<Map<String, String>> handleMailSendingException(MailSendingException ex) {
//        Map<String, String> response = new HashMap<>();
//        response.put("error", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//}
