package com.bezkoder.spring.order;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
	boolean existsByReference(String reference);
	Optional<Order> findByReference(String reference);
}

