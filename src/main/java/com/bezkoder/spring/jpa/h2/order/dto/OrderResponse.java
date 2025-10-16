package com.bezkoder.spring.jpa.h2.order.dto;

import java.time.OffsetDateTime;

import com.bezkoder.spring.jpa.h2.order.OrderStatus;

public record OrderResponse(
    Long id,
    Long userId,
    Long productId,
    Integer quantity,
    OrderStatus status,
    OffsetDateTime createdAt) {
}
