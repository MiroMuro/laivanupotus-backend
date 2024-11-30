package com.miro.Laivanupotus.dto;

import java.time.LocalDateTime;

import com.miro.Laivanupotus.interfaces.UserProfileDto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class NotOwnUserProfileDto implements UserProfileDto {
	private Long id;
	private String userName;
	private int totalGames;
	private int gamesWon;
	private int gamesLost;
	private LocalDateTime lastLogin;

}
