package com.bezkoder.spring.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bezkoder.spring.common.ResourceNotFoundException;
import com.bezkoder.spring.user.User;
import com.bezkoder.spring.user.UserRepository;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public OrderResponse createOrder(OrderRequest request) {
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Order order = new Order(
			request.reference(),
			request.total(),
			request.itemCount(),
			user
		);

		Order saved = orderRepository.save(order);
		return OrderResponse.from(saved);
	}
}

