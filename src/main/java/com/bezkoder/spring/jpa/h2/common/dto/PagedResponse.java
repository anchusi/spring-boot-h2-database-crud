package com.bezkoder.spring.jpa.h2.common.dto;

import java.util.List;

public record PagedResponse<T>(List<T> data, PageMeta meta) {
}
