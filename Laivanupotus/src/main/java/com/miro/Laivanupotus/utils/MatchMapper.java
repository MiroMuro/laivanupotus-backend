package com.miro.Laivanupotus.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.miro.Laivanupotus.dto.ActiveMatchResponseDto;
import com.miro.Laivanupotus.dto.IngameUserProfileDto;
import com.miro.Laivanupotus.dto.AvailableMatchResponseDto;
import com.miro.Laivanupotus.model.Match;

public class MatchMapper {
	public static List<AvailableMatchResponseDto> matchesToDto(List<Match> matches) {
		return matches.stream()
				.map((match) -> matchToDto(match))
				.collect(Collectors.toList());
	};
	public static AvailableMatchResponseDto matchToDto(Match match) {
		String player2UserName = match.getPlayer2() != null
				? match.getPlayer2()
						.getUserName()
						: null;

		return AvailableMatchResponseDto.builder()
				.id(match.getId())
				.player1UserName(match.getPlayer1()
						.getUserName())
				.player2UserName(player2UserName)
				.status(match.getStatus())
				.build();
	};

	public static ActiveMatchResponseDto matchToActiveMatchResponseDto(Match match) {

		return ActiveMatchResponseDto
				.builder()
				.id(match
						.getId())
				.player1(match
						.getPlayer1() != null
						? IngameUserProfileDto
								.builder()
								.id(match
										.getPlayer1()
										.getId())
								.userName(match
										.getPlayer1()
										.getUserName())
								.build()
								: null)
				.player2(match
						.getPlayer2() != null
						? IngameUserProfileDto
								.builder()
								.id(match
										.getPlayer2()
										.getId())
								.userName(match
										.getPlayer2()
										.getUserName())
								.build()
								: null)
				.player1Board(match
						.getPlayer1Board())
				.player2Board(match
						.getPlayer2Board())
				.status(match
						.getStatus())
				.currentTurnPlayerId(match
						.getCurrentTurnPlayerId())
				.startTime(match
						.getStartTime())
				.updatedAt(match
						.getUpdatedAt())
				.endedAt(match
						.getEndedAt())
				.build();

	};


}
