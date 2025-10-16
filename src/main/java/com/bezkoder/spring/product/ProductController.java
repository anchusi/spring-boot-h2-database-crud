package com.bezkoder.spring.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.common.PaginatedResponse;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<PaginatedResponse<ProductResponse>> getProducts(
		@RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be 0 or greater") int page,
		@RequestParam(defaultValue = "20") @Min(value = 1, message = "size must be at least 1")
		@Max(value = 100, message = "size must not exceed 100") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(productService.getProducts(pageable));
	}
}
