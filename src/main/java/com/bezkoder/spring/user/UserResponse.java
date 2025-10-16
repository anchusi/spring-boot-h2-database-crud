package com.bezkoder.spring.user;

public record UserResponse(Long id, String username, String displayName) {
	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getUsername(), user.getDisplayName());
	}
}

