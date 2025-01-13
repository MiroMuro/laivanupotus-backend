package com.miro.Laivanupotus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.dto.LoginRequestDto;
import com.miro.Laivanupotus.dto.NotOwnUserProfileDto;
import com.miro.Laivanupotus.dto.OwnUserProfileDto;
import com.miro.Laivanupotus.dto.UserDto;
import com.miro.Laivanupotus.interfaces.UserProfileDto;
import com.miro.Laivanupotus.model.Player;

@Service
public interface UserService {
	Optional<Player> findById(Long userId);

	Optional<Player> findByUsername(String username);

	Player registerUser(Player user);

	ResponseEntity<OwnUserProfileDto> loginUser(LoginRequestDto loginDto);

	ResponseEntity<List<NotOwnUserProfileDto>> findAllUsersForLeaderboard();

	UserDto userToDto(Player user);

	void logoutUser(Player user);

	UserProfileDto findUserProfile(Long userId, String authToken);
}
