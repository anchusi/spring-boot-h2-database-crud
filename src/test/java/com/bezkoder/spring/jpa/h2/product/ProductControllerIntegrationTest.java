package com.bezkoder.spring.jpa.h2.product;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
    IntStream.rangeClosed(1, 15).forEach(i ->
        productRepository.save(new Product(
            "Product " + i,
            "Description " + i,
            BigDecimal.valueOf(10 + i))));
  }

  @Test
  void shouldReturnPaginatedProductsWithMetadata() throws Exception {
    mockMvc.perform(get("/api/products").param("page", "1").param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.totalElements", equalTo(15)))
        .andExpect(jsonPath("$.meta.totalPages", equalTo(3)))
        .andExpect(jsonPath("$.meta.page", equalTo(1)))
        .andExpect(jsonPath("$.meta.size", equalTo(5)))
        .andExpect(jsonPath("$.data", hasSize(5)));
  }

  @Test
  void shouldUseDefaultPaginationWhenParamsMissing() throws Exception {
    mockMvc.perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.page", equalTo(0)))
        .andExpect(jsonPath("$.meta.size", equalTo(10)))
        .andExpect(jsonPath("$.data", hasSize(10)));
  }
}
