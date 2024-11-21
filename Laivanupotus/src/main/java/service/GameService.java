package service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import model.Match;
import model.Move;
import model.Ship;
import model.User;

@Service
public interface GameService {
	List<Match> findAvailableMatches();

	Match joinMatch(Long matchId, User player);

	Optional<Match> getMatchById(Long matchId);

	Match placeShips(Long matchId, Long playerId, List<Ship> ships);

	Move makeMove(Long matchId, Long playerId, Move move);

	Match createMatch(User player);
}
