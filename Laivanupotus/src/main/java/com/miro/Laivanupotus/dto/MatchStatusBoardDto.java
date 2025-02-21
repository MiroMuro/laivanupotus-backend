package com.miro.Laivanupotus.dto;

import java.util.List;

import com.miro.Laivanupotus.model.Move;
import com.miro.Laivanupotus.model.Ship;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class MatchStatusBoardDto {
	private Long id;
	private List<Ship> ships;
	private List<Move> moves;
}
