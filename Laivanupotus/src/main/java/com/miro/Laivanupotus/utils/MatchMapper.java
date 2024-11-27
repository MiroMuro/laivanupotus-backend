package com.miro.Laivanupotus.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.miro.Laivanupotus.dto.MatchResponseDto;
import com.miro.Laivanupotus.model.Match;

public class MatchMapper {
	public static List<MatchResponseDto> matchesToDto(List<Match> matches) {
		return matches.stream()
				.map((match) -> matchToDto(match))
				.collect(Collectors.toList());
	};
	static MatchResponseDto matchToDto(Match match) {
		String player2UserName = match.getPlayer2() != null
				? match.getPlayer2()
						.getUserName()
						: null;

		return MatchResponseDto.builder()
				.id(match.getId())
				.player1UserName(match.getPlayer1()
						.getUserName())
				.player2UserName(player2UserName)
				.status(match.getStatus())
				.build();
	};


}
