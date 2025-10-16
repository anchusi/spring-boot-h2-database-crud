package com.bezkoder.spring.jpa.h2.common.error;

public record FieldValidationError(String field, String message) {
}
