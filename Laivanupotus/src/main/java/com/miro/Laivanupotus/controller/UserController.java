package com.miro.Laivanupotus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miro.Laivanupotus.dto.UserDto;
import com.miro.Laivanupotus.model.User;
import com.miro.Laivanupotus.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<UserDto> registerNewUser(@RequestBody User user) {
		User newRegisteredUser = userService.registerUser(user);
		UserDto newRegisteredUserDto = userService.userToDto(newRegisteredUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(newRegisteredUserDto);
	};

	@PostMapping("/login")
	public ResponseEntity<User> loginUser(@RequestBody String userName, String passWord) {
		User loggedInUser = userService.loginUser(userName, passWord);
		return ResponseEntity.ok(loggedInUser);
	};

	@PostMapping("/logout")
	public ResponseEntity<Object> logOutUser(@RequestBody User user) {
		userService.logoutUser(user);
		return ResponseEntity.ok().build();
	};

	@PostMapping("/{userId}/profile")
	public ResponseEntity<User> getUserProfile(@PathVariable Long userId) {
		User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
		return ResponseEntity.ok(user);
	};
}
