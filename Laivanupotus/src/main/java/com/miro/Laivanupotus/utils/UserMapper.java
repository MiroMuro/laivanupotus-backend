package com.miro.Laivanupotus.utils;

import com.miro.Laivanupotus.dto.NotOwnUserProfileDto;
import com.miro.Laivanupotus.dto.OwnUserProfileDto;
import com.miro.Laivanupotus.dto.UserDto;
import com.miro.Laivanupotus.model.User;

public class UserMapper {

	public UserDto userToDto(User user) {
		UserDto userDto = new UserDto(user.getUserName(), user.getEmail());
		return userDto;
	}

	public static NotOwnUserProfileDto userToNotOwnUserProfileDto(User user) {
		return NotOwnUserProfileDto.builder()
				.userName(user.getUserName())
				.totalGames(user.getTotalGames())
				.gamesWon(user.getGamesWon())
				.gamesLost(user.getGamesLost())
				.lastLogin(user.getLastLogin())
				.build();
	};

	public static OwnUserProfileDto userToOwnUserProfileDto(User user) {
		return OwnUserProfileDto.builder()
				.userName(user.getUserName())
				.email(user.getEmail())
				.totalGames(user.getTotalGames())
				.gamesWon(user.getGamesWon())
				.gamesLost(user.getGamesLost())
				.createdAt(user.getCreatedAt())
				.build();
	};
}
