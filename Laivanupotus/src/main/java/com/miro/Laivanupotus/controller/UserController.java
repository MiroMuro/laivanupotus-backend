package com.miro.Laivanupotus.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miro.Laivanupotus.dto.LoginRequestDto;
import com.miro.Laivanupotus.dto.NotOwnUserProfileDto;
import com.miro.Laivanupotus.dto.OwnUserProfileDto;
import com.miro.Laivanupotus.dto.UserDto;
import com.miro.Laivanupotus.interfaces.UserProfileDto;
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
	public ResponseEntity<OwnUserProfileDto> loginUser(
			@RequestBody LoginRequestDto loginDto) {
		// Returns auth token as headers, if login successful.
		ResponseEntity<OwnUserProfileDto> loginResponse = userService
				.loginUser(loginDto);

		return loginResponse;
	};

	@PostMapping("/logout")
	public ResponseEntity<Object> logOutUser(@RequestBody User user) {
		userService.logoutUser(user);
		return ResponseEntity.ok().build();
	};

	@GetMapping("/{userId}/profile")
	public ResponseEntity<UserProfileDto> getUserProfile(
			@PathVariable Long userId,
			@RequestHeader("Authorization") String authHeader) {

		// Return user profile info.
		UserProfileDto userProfile = userService
				.findUserProfile(userId, authHeader);

		return ResponseEntity.ok(userProfile);
	};
	@GetMapping("/leaderboard")
	public ResponseEntity<List<NotOwnUserProfileDto>> getLeaderBoardUserDtoProfiles() {
		return userService.findAllUsersForLeaderboard();
	};
}
