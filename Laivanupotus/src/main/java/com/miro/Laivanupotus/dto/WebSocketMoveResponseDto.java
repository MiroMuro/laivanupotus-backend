package com.miro.Laivanupotus.dto;

import com.miro.Laivanupotus.model.Move;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebSocketMoveResponseDto {
	private Move move;
	private String message;
}
