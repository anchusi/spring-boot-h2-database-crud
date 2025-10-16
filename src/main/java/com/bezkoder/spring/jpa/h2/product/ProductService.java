package com.bezkoder.spring.jpa.h2.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bezkoder.spring.jpa.h2.common.dto.PageMeta;
import com.bezkoder.spring.jpa.h2.common.dto.PagedResponse;
import com.bezkoder.spring.jpa.h2.product.dto.ProductResponse;

@Service
public class ProductService {

  private final ProductRepository repository;

  public ProductService(ProductRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public PagedResponse<ProductResponse> getProducts(Pageable pageable) {
    Page<Product> page = repository.findAll(pageable);
    List<ProductResponse> data = page
        .map(product -> new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.isActive(),
            product.getCreatedAt()))
        .getContent();

    PageMeta meta = new PageMeta(
        page.getTotalElements(),
        page.getTotalPages(),
        page.getNumber(),
        page.getSize());

    return new PagedResponse<>(data, meta);
  }
}
