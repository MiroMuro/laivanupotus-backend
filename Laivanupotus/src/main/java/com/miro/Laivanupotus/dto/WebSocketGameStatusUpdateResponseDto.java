package com.miro.Laivanupotus.dto;

import com.miro.Laivanupotus.Enums.GameStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebSocketGameStatusUpdateResponseDto {
    public IngameUserProfileDto player;
    public GameStatus status;
}
