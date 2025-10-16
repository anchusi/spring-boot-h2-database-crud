package com.bezkoder.spring.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bezkoder.spring.common.ResourceNotFoundException;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return UserResponse.from(user);
	}
}

