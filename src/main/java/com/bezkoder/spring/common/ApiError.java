package com.bezkoder.spring.common;

import java.time.Instant;
import java.util.List;

public record ApiError(
	Instant timestamp,
	int status,
	String error,
	String path,
	List<FieldValidationError> fieldErrors
) {
}

