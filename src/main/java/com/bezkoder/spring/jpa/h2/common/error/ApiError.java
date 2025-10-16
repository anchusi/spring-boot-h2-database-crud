package com.bezkoder.spring.jpa.h2.common.error;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
    int status,
    String code,
    String message,
    OffsetDateTime timestamp,
    List<FieldValidationError> errors) {

  public ApiError withErrors(List<FieldValidationError> fieldErrors) {
    return new ApiError(status, code, message, timestamp, fieldErrors);
  }
}
