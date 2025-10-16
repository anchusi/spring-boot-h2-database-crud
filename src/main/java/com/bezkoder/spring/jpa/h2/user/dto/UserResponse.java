package com.bezkoder.spring.jpa.h2.user.dto;

public record UserResponse(Long id, String name, String email, String role) {
}
