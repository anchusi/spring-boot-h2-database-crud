package com.bezkoder.spring.common;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request
	) {
		List<FieldValidationError> fieldErrors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(this::mapFieldError)
			.toList();

		return buildResponse(
			HttpStatus.BAD_REQUEST,
			"Validation failed",
			request,
			fieldErrors
		);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
		Exception ex,
		Object body,
		HttpHeaders headers,
		HttpStatusCode statusCode,
		WebRequest request
	) {
		return buildResponse(
			HttpStatus.valueOf(statusCode.value()),
			ex.getMessage(),
			request,
			List.of()
		);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFound(
		ResourceNotFoundException ex,
		HttpServletRequest request
	) {
		return buildResponse(
			HttpStatus.NOT_FOUND,
			ex.getMessage(),
			request,
			List.of()
		);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolation(
		ConstraintViolationException ex,
		HttpServletRequest request
	) {
		List<FieldValidationError> fieldErrors = ex.getConstraintViolations()
			.stream()
			.map(this::mapConstraintViolation)
			.collect(Collectors.toList());

		return buildResponse(
			HttpStatus.BAD_REQUEST,
			"Validation failed",
			request,
			fieldErrors
		);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDenied(
		AccessDeniedException ex,
		HttpServletRequest request
	) {
		return buildResponse(
			HttpStatus.FORBIDDEN,
			"Access is denied",
			request,
			List.of()
		);
	}

	private ResponseEntity<ApiError> buildResponse(
		HttpStatus status,
		String message,
		WebRequest request,
		List<FieldValidationError> fieldErrors
	) {
		String path = request instanceof ServletWebRequest servletRequest
			? servletRequest.getRequest().getRequestURI()
			: "";
		ApiError apiError = new ApiError(
			Instant.now(),
			status.value(),
			message,
			path,
			fieldErrors
		);
		return ResponseEntity.status(status).body(apiError);
	}

	private ResponseEntity<ApiError> buildResponse(
		HttpStatus status,
		String message,
		HttpServletRequest request,
		List<FieldValidationError> fieldErrors
	) {
		ApiError apiError = new ApiError(
			Instant.now(),
			status.value(),
			message,
			request.getRequestURI(),
			fieldErrors
		);
		return ResponseEntity.status(status).body(apiError);
	}

	private FieldValidationError mapFieldError(FieldError fieldError) {
		return new FieldValidationError(fieldError.getField(), fieldError.getDefaultMessage());
	}

	private FieldValidationError mapConstraintViolation(ConstraintViolation<?> violation) {
		String field = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "";
		return new FieldValidationError(field, violation.getMessage());
	}
}
