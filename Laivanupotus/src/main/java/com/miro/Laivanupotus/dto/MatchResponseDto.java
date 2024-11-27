package com.miro.Laivanupotus.dto;

import com.miro.Laivanupotus.model.Match.GameStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class MatchResponseDto {
	private Long id;
	private String player1UserName;
	private String player2UserName;
	@Enumerated(EnumType.STRING)
	private GameStatus status;
}
