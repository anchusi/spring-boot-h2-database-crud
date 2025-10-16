package com.bezkoder.spring.jpa.h2.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.jpa.h2.common.dto.PagedResponse;
import com.bezkoder.spring.jpa.h2.product.dto.ProductResponse;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 10;

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<PagedResponse<ProductResponse>> listProducts(
      @RequestParam(name = "page", defaultValue = "" + DEFAULT_PAGE) int page,
      @RequestParam(name = "size", defaultValue = "" + DEFAULT_SIZE) int size) {

    int sanitizedPage = Math.max(page, 0);
    int sanitizedSize = size < 1 ? DEFAULT_SIZE : Math.min(size, 50);

    Pageable pageable = PageRequest.of(sanitizedPage, sanitizedSize, Sort.by("createdAt").descending());
    return ResponseEntity.ok(productService.getProducts(pageable));
  }
}
