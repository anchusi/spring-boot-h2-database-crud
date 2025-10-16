package com.bezkoder.spring.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bezkoder.spring.common.PaginatedResponse;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginatedResponse<ProductResponse> getProducts(Pageable pageable) {
		Pageable effective = pageable.getSort().isUnsorted()
			? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending())
			: pageable;

		Page<Product> page = productRepository.findAll(effective);

		return new PaginatedResponse<>(
			page.map(ProductResponse::from).getContent(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.isFirst(),
			page.isLast()
		);
	}
}

