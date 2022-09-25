package com.example.demo.web;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> validationFailed(ConstraintViolationException ex, ServletWebRequest req) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("path", req.getRequest().getRequestURI());
        errors.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        errors.put("code", "validation_failure");
        errors.put("message", ex.getMessage());
        errors.put("errors", ex.getConstraintViolations().stream()
                .map(cv -> Map.of("field", cv.getPropertyPath(), "message", cv.getMessage())));
        log.debug("validation errors: {}", errors);

        return status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
    }


}
