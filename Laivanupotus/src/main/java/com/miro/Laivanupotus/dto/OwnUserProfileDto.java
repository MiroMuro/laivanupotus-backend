package com.miro.Laivanupotus.dto;

import java.time.LocalDateTime;

import com.miro.Laivanupotus.interfaces.UserProfileDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnUserProfileDto implements UserProfileDto {
	private String userName;
	private String email;
	private int totalGames;
	private int gamesWon;
	private int gamesLost;
	private LocalDateTime createdAt;

}
