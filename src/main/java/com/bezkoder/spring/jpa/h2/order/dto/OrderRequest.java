package com.bezkoder.spring.jpa.h2.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
    @NotNull(message = "userId is required") Long userId,
    @NotNull(message = "productId is required") Long productId,
    @NotNull(message = "quantity is required") @Min(value = 1, message = "quantity must be at least 1") Integer quantity) {
}
