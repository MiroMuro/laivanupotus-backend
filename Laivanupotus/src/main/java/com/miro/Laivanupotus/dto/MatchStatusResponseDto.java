package com.miro.Laivanupotus.dto;

import com.miro.Laivanupotus.Enums.GameStatus;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
//This class is used to send match status to the client after the user has refreshed the page or reconnects to the match.
public class MatchStatusResponseDto {
	private Long id;
	private IngameUserProfileDto player1;
	private IngameUserProfileDto player2;
	private MatchStatusBoardDto player1Board;
	private MatchStatusBoardDto player2Board;
	private Long currentTurnPlayerId;
	private GameStatus status;
}
