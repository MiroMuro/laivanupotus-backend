package com.miro.Laivanupotus.dto;

import java.time.LocalDateTime;

import com.miro.Laivanupotus.Enums.GameStatus;
import com.miro.Laivanupotus.model.Board;

import lombok.Data;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
public class ActiveMatchResponseDto {

    private Long id;

    private IngameUserProfileDto player1;

    private IngameUserProfileDto player2;

    private Board player1Board;

    private Board player2Board;

    private GameStatus status;

    private Long currentTurnPlayerId;

    private LocalDateTime startTime;
    private LocalDateTime updatedAt;
    private LocalDateTime endedAt;


}
