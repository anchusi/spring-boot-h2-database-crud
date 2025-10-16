package com.bezkoder.spring.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.order.OrderRepository;
import com.bezkoder.spring.product.ProductRepository;
import com.bezkoder.spring.user.UserRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;

	public AdminController(
		UserRepository userRepository,
		ProductRepository productRepository,
		OrderRepository orderRepository
	) {
		this.userRepository = userRepository;
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
	}

	@GetMapping("/overview")
	public AdminOverviewResponse getOverview() {
		return new AdminOverviewResponse(
			userRepository.count(),
			productRepository.count(),
			orderRepository.count()
		);
	}
}

