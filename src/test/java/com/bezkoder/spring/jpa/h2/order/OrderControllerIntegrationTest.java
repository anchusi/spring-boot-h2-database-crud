package com.bezkoder.spring.jpa.h2.order;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bezkoder.spring.jpa.h2.order.dto.OrderRequest;
import com.bezkoder.spring.jpa.h2.product.Product;
import com.bezkoder.spring.jpa.h2.product.ProductRepository;
import com.bezkoder.spring.jpa.h2.user.UserAccount;
import com.bezkoder.spring.jpa.h2.user.UserAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PurchaseOrderRepository orderRepository;

  @Autowired
  private UserAccountRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    orderRepository.deleteAll();
    productRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void shouldCreateOrder() throws Exception {
    UserAccount user = userRepository.save(new UserAccount("Order User", "order.user@example.com", "ROLE_USER"));
    Product product = productRepository
        .save(new Product("Test Product", "Description", java.math.BigDecimal.valueOf(9.99)));

    OrderRequest request = new OrderRequest(user.getId(), product.getId(), 2);

    mockMvc.perform(post("/api/orders")
        .with(httpBasic("user", "user123"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString("/api/orders/")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.userId", equalTo(user.getId().intValue())))
        .andExpect(jsonPath("$.productId", equalTo(product.getId().intValue())))
        .andExpect(jsonPath("$.quantity", equalTo(2)))
        .andExpect(jsonPath("$.status", equalTo("CREATED")));
  }

  @Test
  void shouldReturnValidationErrorsWhenPayloadInvalid() throws Exception {
    Product product = productRepository
        .save(new Product("Another Product", "Description", java.math.BigDecimal.valueOf(15.00)));

    OrderRequest request = new OrderRequest(null, product.getId(), 0);

    mockMvc.perform(post("/api/orders")
        .with(httpBasic("user", "user123"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code", equalTo("VALIDATION_ERROR")))
        .andExpect(jsonPath("$.errors", notNullValue()))
        .andExpect(jsonPath("$.errors[0].field", notNullValue()));
  }

  @Test
  void shouldReturnNotFoundWhenRelatedEntitiesMissing() throws Exception {
    OrderRequest request = new OrderRequest(999L, 888L, 1);

    mockMvc.perform(post("/api/orders")
        .with(httpBasic("user", "user123"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")))
        .andExpect(jsonPath("$.message", equalTo("Requested resource was not found")));
  }
}
