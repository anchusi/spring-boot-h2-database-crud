package com.bezkoder.spring.product;

import org.springframework.data.domain.Pageable;

import com.bezkoder.spring.common.PaginatedResponse;

public interface ProductService {
	PaginatedResponse<ProductResponse> getProducts(Pageable pageable);
}

