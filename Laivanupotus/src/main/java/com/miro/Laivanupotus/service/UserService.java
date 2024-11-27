package com.miro.Laivanupotus.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.dto.LoginRequestDto;
import com.miro.Laivanupotus.dto.UserDto;
import com.miro.Laivanupotus.model.User;

@Service
public interface UserService {
	Optional<User> findById(Long userId);

	Optional<User> findByUsername(String username);

	User registerUser(User user);

	ResponseEntity<String> loginUser(LoginRequestDto loginDto);

	UserDto userToDto(User user);

	void logoutUser(User user);
}
