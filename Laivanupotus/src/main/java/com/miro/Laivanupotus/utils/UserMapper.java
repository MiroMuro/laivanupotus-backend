package com.miro.Laivanupotus.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.miro.Laivanupotus.dto.IngameUserProfileDto;
import com.miro.Laivanupotus.dto.NotOwnUserProfileDto;
import com.miro.Laivanupotus.dto.OwnUserProfileDto;
import com.miro.Laivanupotus.dto.UserDto;
import com.miro.Laivanupotus.dto.UserIdNameDto;
import com.miro.Laivanupotus.interfaces.UserProfileDto;
import com.miro.Laivanupotus.model.Player;

public class UserMapper {

    public UserDto userToDto(Player user) {
	UserDto userDto = new UserDto(user.getUserName(), user.getEmail());
	return userDto;
    }
    
    
    public static UserProfileDto userToIngameUserProfileDto(
	    UserProfileDto userProfileDto) {
	return IngameUserProfileDto
		.builder()
		.id(userProfileDto
			.getId())
		.userName(userProfileDto
			.getUserName())
		.build();
    }
    
    public static NotOwnUserProfileDto userToNotOwnUserProfileDto(Player user) {
	return NotOwnUserProfileDto.builder()
		.userName(user.getUserName())
		.totalGames(user.getTotalGames())
		.gamesWon(user.getGamesWon())
		.gamesLost(user.getGamesLost())
		.lastLogin(user.getLastLogin())
		.build();
    };

    public static OwnUserProfileDto userToOwnUserProfileDto(Player user) {
	return OwnUserProfileDto.builder()
		.id(user
			.getId())
		.userName(user.getUserName())
		.email(user.getEmail())
		.totalGames(user.getTotalGames())
		.gamesWon(user.getGamesWon())
		.gamesLost(user.getGamesLost())
		.createdAt(user.getCreatedAt())
		.build();
    };

    public static List<NotOwnUserProfileDto> usersToNotOwnUserProfileDto(
	    List<Player> users) {
	return users.stream().map((user) -> userToNotOwnUserProfileDto(user))
		.collect(Collectors.toList());
    }
}
