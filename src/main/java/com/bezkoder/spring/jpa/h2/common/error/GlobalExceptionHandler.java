package com.bezkoder.spring.jpa.h2.common.error;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
    List<FieldValidationError> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fieldError -> new FieldValidationError(fieldError.getField(), resolveMessage(fieldError)))
        .collect(Collectors.toList());

    ApiError body = new ApiError(
        HttpStatus.BAD_REQUEST.value(),
        "VALIDATION_ERROR",
        "Validation failed",
        OffsetDateTime.now(),
        errors);

    log.debug("Validation error: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
    List<FieldValidationError> errors = ex.getConstraintViolations()
        .stream()
        .map(this::toFieldError)
        .collect(Collectors.toList());

    ApiError body = new ApiError(
        HttpStatus.BAD_REQUEST.value(),
        "VALIDATION_ERROR",
        "Validation failed",
        OffsetDateTime.now(),
        errors);

    log.debug("Constraint violation: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
    ApiError body = new ApiError(
        HttpStatus.NOT_FOUND.value(),
        "NOT_FOUND",
        "Requested resource was not found",
        OffsetDateTime.now(),
        List.of());

    log.debug("Resource not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    FieldValidationError error = new FieldValidationError(ex.getName(), "Invalid value");
    ApiError body = new ApiError(
        HttpStatus.BAD_REQUEST.value(),
        "VALIDATION_ERROR",
        "Validation failed",
        OffsetDateTime.now(),
        List.of(error));

    log.debug("Type mismatch: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
    ApiError body = new ApiError(
        HttpStatus.FORBIDDEN.value(),
        "ACCESS_DENIED",
        "You do not have permission to perform this action",
        OffsetDateTime.now(),
        List.of());

    log.debug("Access denied: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest request) {
    ApiError body = new ApiError(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "INTERNAL_ERROR",
        "An unexpected error occurred",
        OffsetDateTime.now(),
        List.of());

    log.error("Unhandled exception at {}: {}", request.getDescription(false), ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  private String resolveMessage(FieldError fieldError) {
    String message = fieldError.getDefaultMessage();
    return message != null ? message : "Invalid value";
  }

  private FieldValidationError toFieldError(ConstraintViolation<?> violation) {
    String fieldPath = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "";
    return new FieldValidationError(fieldPath, violation.getMessage());
  }
}
