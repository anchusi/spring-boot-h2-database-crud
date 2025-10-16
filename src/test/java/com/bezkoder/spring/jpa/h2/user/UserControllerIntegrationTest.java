package com.bezkoder.spring.jpa.h2.user;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserAccountRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void shouldReturnUserById() throws Exception {
    UserAccount user = userRepository.save(new UserAccount("Test User", "test.user@example.com", "ROLE_USER"));

    mockMvc.perform(get("/api/users/{id}", user.getId()).with(httpBasic("user", "user123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", equalTo(user.getId().intValue())))
        .andExpect(jsonPath("$.name", equalTo("Test User")))
        .andExpect(jsonPath("$.email", equalTo("test.user@example.com")))
        .andExpect(jsonPath("$.role", equalTo("ROLE_USER")));
  }

  @Test
  void shouldReturnNotFoundForMissingUser() throws Exception {
    mockMvc.perform(get("/api/users/{id}", 999L).with(httpBasic("user", "user123")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")))
        .andExpect(jsonPath("$.message", equalTo("Requested resource was not found")))
        .andExpect(jsonPath("$.errors", notNullValue()));
  }
}
