package com.bezkoder.spring.jpa.h2.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bezkoder.spring.jpa.h2.order.dto.OrderRequest;
import com.bezkoder.spring.jpa.h2.order.dto.OrderResponse;
import com.bezkoder.spring.jpa.h2.product.Product;
import com.bezkoder.spring.jpa.h2.product.ProductRepository;
import com.bezkoder.spring.jpa.h2.user.UserAccount;
import com.bezkoder.spring.jpa.h2.user.UserAccountRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderService {

  private final PurchaseOrderRepository orderRepository;
  private final UserAccountRepository userRepository;
  private final ProductRepository productRepository;

  public OrderService(
      PurchaseOrderRepository orderRepository,
      UserAccountRepository userRepository,
      ProductRepository productRepository) {
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
    this.productRepository = productRepository;
  }

  @Transactional
  public OrderResponse createOrder(OrderRequest request) {
    UserAccount user = userRepository.findById(request.userId())
        .orElseThrow(() -> new EntityNotFoundException("User " + request.userId() + " was not found"));
    Product product = productRepository.findById(request.productId())
        .orElseThrow(() -> new EntityNotFoundException("Product " + request.productId() + " was not found"));

    PurchaseOrder purchaseOrder = new PurchaseOrder(user, product, request.quantity());
    PurchaseOrder savedOrder = orderRepository.save(purchaseOrder);

    return new OrderResponse(
        savedOrder.getId(),
        savedOrder.getUser().getId(),
        savedOrder.getProduct().getId(),
        savedOrder.getQuantity(),
        savedOrder.getStatus(),
        savedOrder.getCreatedAt());
  }
}
