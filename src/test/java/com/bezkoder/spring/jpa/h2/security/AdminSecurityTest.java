package com.bezkoder.spring.jpa.h2.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.bezkoder.spring.jpa.h2.user.UserAccount;
import com.bezkoder.spring.jpa.h2.user.UserAccountRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AdminSecurityTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserAccountRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    userRepository.save(new UserAccount("Admin User", "admin.app@example.com", "ROLE_ADMIN"));
  }

  @Test
  void adminShouldAccessProtectedEndpoint() throws Exception {
    mockMvc.perform(get("/api/admin/users").with(httpBasic("admin", "admin123")))
        .andExpect(status().isOk());
  }

  @Test
  void nonAdminShouldNotAccessProtectedEndpoint() throws Exception {
    mockMvc.perform(get("/api/admin/users").with(httpBasic("user", "user123")))
        .andExpect(status().isForbidden());
  }

  @Test
  void unauthenticatedRequestShouldBeRejected() throws Exception {
    mockMvc.perform(get("/api/admin/users"))
        .andExpect(status().isUnauthorized());
  }
}
