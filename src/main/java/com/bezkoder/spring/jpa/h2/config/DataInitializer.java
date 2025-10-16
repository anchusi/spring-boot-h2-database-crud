package com.bezkoder.spring.jpa.h2.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.bezkoder.spring.jpa.h2.product.Product;
import com.bezkoder.spring.jpa.h2.product.ProductRepository;
import com.bezkoder.spring.jpa.h2.user.UserAccount;
import com.bezkoder.spring.jpa.h2.user.UserAccountRepository;

@Component
public class DataInitializer implements ApplicationRunner {

  private final UserAccountRepository userRepository;
  private final ProductRepository productRepository;

  public DataInitializer(UserAccountRepository userRepository, ProductRepository productRepository) {
    this.userRepository = userRepository;
    this.productRepository = productRepository;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (userRepository.count() == 0) {
      userRepository.saveAll(List.of(
          new UserAccount("Alice Johnson", "alice@example.com", "ROLE_USER"),
          new UserAccount("Bob Smith", "bob@example.com", "ROLE_ADMIN")));
    }

    if (productRepository.count() == 0) {
      productRepository.saveAll(List.of(
          new Product("Wireless Mouse", "Ergonomic wireless mouse", new BigDecimal("29.99")),
          new Product("Mechanical Keyboard", "Backlit mechanical keyboard", new BigDecimal("119.00")),
          new Product("USB-C Hub", "Multi-port USB-C hub", new BigDecimal("59.50"))));
    }
  }
}
