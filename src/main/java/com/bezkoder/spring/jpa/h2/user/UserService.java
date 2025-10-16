package com.bezkoder.spring.jpa.h2.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bezkoder.spring.jpa.h2.user.dto.UserResponse;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

  private final UserAccountRepository repository;

  public UserService(UserAccountRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long id) {
    UserAccount user = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User " + id + " was not found"));
    return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
  }

  @Transactional(readOnly = true)
  public java.util.List<UserResponse> getAllUsers() {
    return repository.findAll()
        .stream()
        .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole()))
        .toList();
  }
}
