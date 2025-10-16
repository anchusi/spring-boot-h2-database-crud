package com.bezkoder.spring.jpa.h2.product.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    boolean active,
    OffsetDateTime createdAt) {
}
