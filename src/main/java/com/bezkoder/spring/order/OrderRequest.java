package com.bezkoder.spring.order;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderRequest(
	@NotNull(message = "userId is required")
	Long userId,

	@NotBlank(message = "reference is required")
	@Size(max = 40, message = "reference must be at most 40 characters")
	String reference,

	@NotNull(message = "total is required")
	@DecimalMin(value = "0.01", inclusive = true, message = "total must be greater than zero")
	BigDecimal total,

	@NotNull(message = "itemCount is required")
	@Min(value = 1, message = "itemCount must be at least 1")
	Integer itemCount
) {
}

