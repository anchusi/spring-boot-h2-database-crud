package com.bezkoder.spring.jpa.h2.common.dto;

public record PageMeta(long totalElements, int totalPages, int page, int size) {
}
