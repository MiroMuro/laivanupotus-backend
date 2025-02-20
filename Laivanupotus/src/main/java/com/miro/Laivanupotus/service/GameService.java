package com.miro.Laivanupotus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.Enums.GameStatus;
import com.miro.Laivanupotus.dto.ActiveMatchResponseDto;
import com.miro.Laivanupotus.dto.AvailableMatchResponseDto;
import com.miro.Laivanupotus.model.PlayerConnectionMessage;
import com.miro.Laivanupotus.model.Match;
import com.miro.Laivanupotus.model.Move;
import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.model.Ship;

@Service
public interface GameService {
    List<AvailableMatchResponseDto> findAvailableMatches();

    ActiveMatchResponseDto joinMatch(Long matchId, Player player);

    Optional<Match> getMatchById(Long matchId);

    Match placeShips(Long matchId, Long playerId, List<Ship> ships);

    Move makeMove(Long matchId, Long playerId, Move move);

    boolean authorizeMatch(Long matchId, Long playerId);

    ActiveMatchResponseDto createMatch(Player player);
    
    ActiveMatchResponseDto getActiveMatchByUserIdAndMatchId(Long matchId, Long playerId);
    
    
    void  disconnectPlayer(PlayerConnectionMessage disconnectMessage, Long matchId);
}
