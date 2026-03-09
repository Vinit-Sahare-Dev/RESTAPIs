package com.spring.restapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;


@RestControllerAdvice
public class EmployeeControllerAdvice {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleNotFound(EmployeeNotFoundException ex) {
        APIErrorResponse apiError = new APIErrorResponse();
        apiError.setStatusCode(HttpStatus.NOT_FOUND.value());
        apiError.setMessage(ex.getMessage());
        apiError.setDateTime(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("Error-Info", "employee not found")
                .body(apiError);
    }

    @ExceptionHandler(IllegalDepartmentException.class)
    public ResponseEntity<APIErrorResponse> handleIllegalDept(IllegalDepartmentException ex) {
        APIErrorResponse apiError = new APIErrorResponse();
        apiError.setStatusCode(HttpStatus.BAD_REQUEST.value());
        apiError.setMessage(ex.getMessage());
        apiError.setDateTime(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Error-Info", "invalid department")
                .body(apiError);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }

        APIErrorResponse apiError = new APIErrorResponse();
        apiError.setStatusCode(HttpStatus.BAD_REQUEST.value());
        apiError.setMessage(validationErrors.toString());  // You can customize formatting if needed
        apiError.setDateTime(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Error-Info", "validation failed")
                .body(apiError);
    }
}
