package com.bezkoder.spring.order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
	Long id,
	String reference,
	BigDecimal total,
	Integer itemCount,
	Instant createdAt,
	Long userId
) {
	public static OrderResponse from(Order order) {
		return new OrderResponse(
			order.getId(),
			order.getReference(),
			order.getTotal(),
			order.getItemCount(),
			order.getCreatedAt(),
			order.getUser().getId()
		);
	}
}

