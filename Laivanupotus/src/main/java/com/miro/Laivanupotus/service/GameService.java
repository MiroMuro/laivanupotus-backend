package com.miro.Laivanupotus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.model.Match;
import com.miro.Laivanupotus.model.Move;
import com.miro.Laivanupotus.model.Ship;
import com.miro.Laivanupotus.model.User;

@Service
public interface GameService {
	List<Match> findAvailableMatches();

	Match joinMatch(Long matchId, User player);

	Optional<Match> getMatchById(Long matchId);

	Match placeShips(Long matchId, Long playerId, List<Ship> ships);

	Move makeMove(Long matchId, Long playerId, Move move);

	Match createMatch(User player);
}
