package com.example.demo.web;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.net.URI;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex, ServletWebRequest req) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Unprocessable Entity");
        problem.setType(URI.create("https://example.com/errors/unprocessable_entity"));
        problem.setProperty("violations",
                ex.getConstraintViolations()
                        .stream()
                        .map(cv -> Map.of("field", cv.getPropertyPath(), "message", cv.getMessage()))
                        .toList()
        );

        return problem;
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, ServletWebRequest req) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Unprocessable Entity");
        problem.setType(URI.create("https://example.com/errors/unprocessable_entity"));
        problem.setProperty("violations",
                ex.getFieldErrors()
                        .stream()
                        .map(cv -> Map.of("field", cv.getField(), "message", cv.getDefaultMessage()))
                        .toList()
        );

        return problem;
    }


}
