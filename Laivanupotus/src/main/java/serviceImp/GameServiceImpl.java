package serviceImp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import model.Board;
import model.Match;
import model.Match.GameStatus;
import model.Move;
import model.Ship;
import model.User;
import repository.MatchRepository;
import service.GameService;

@Service
public class GameServiceImpl implements GameService {

	private final MatchRepository matchRepository;

	public GameServiceImpl(MatchRepository matchRepository) {
		this.matchRepository = matchRepository;
	}

	@Override
	public List<Match> findAvailableMatches() {
		return matchRepository.findByStatus(GameStatus.WAITING_FOR_PLAYER);
	}

	@Override
	public Match joinMatch(Long matchId, User player) {
		Match matchRequiringPlayer2 = matchRepository.findById(matchId)
				.orElseThrow(() -> new RuntimeException("Match not found!"));

		if (matchRequiringPlayer2.getStatus() != Match.GameStatus.WAITING_FOR_PLAYER) {
			throw new RuntimeException("Match is not waiting for a player!");
		}

		if (matchRequiringPlayer2.getPlayer1().getId().equals(player.getId())) {
			throw new RuntimeException("You cannot join your own match!");
		}

		matchRequiringPlayer2.setPlayer2(player);
		matchRequiringPlayer2.setStatus(GameStatus.PLACING_SHIPS);
		matchRequiringPlayer2.setUpdatedAt(LocalDateTime.now());

		Board player2Board = new Board();
		player2Board.setBoardState(".".repeat(100));
		matchRequiringPlayer2.setPlayer2Board(player2Board);

		return matchRepository.save(matchRequiringPlayer2);
	}

	@Override
	public Optional<Match> getMatchById(Long matchId) {
		return matchRepository.findById(matchId);
	}

	@Override
	public Match placeShips(Long matchId, Long playerId, List<Ship> ships) {
		Match match = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match not found!"));

		if (match.getStatus() != GameStatus.PLACING_SHIPS) {
			throw new RuntimeException("Match is not in the placing ships phase!");
		}

		if (playerId == match.getPlayer1().getId()) {
			Board player1Board = match.getPlayer1Board();
		} else {
			Board player2Board = match.getPlayer2Board();
		}

		return null;
	}

	@Override
	public Move makeMove(Long matchId, Long playerId, Move move) {
		return null;
	}

	@Override
	public Match createMatch(User player) {
		Match newMatch = new Match();
		newMatch.setPlayer1(player);
		newMatch.setStatus(GameStatus.WAITING_FOR_PLAYER);
		newMatch.setStartTime(LocalDateTime.now());
		newMatch.setUpdatedAt(LocalDateTime.now());

		// Initialize the board for player 1.
		Board player1Board = new Board();
		player1Board.setBoardState(".".repeat(100));
		newMatch.setPlayer1Board(player1Board);
		return matchRepository.save(newMatch);
	}

	public void placeShip(Board board, Ship ship) {
		int shipIndex = ship.getX();
		int shipLength = ship.getType().getLength();
		String originalBoardState = board.getBoardState();

		char shipChar = 'X';

	};

	public void placeVerticalShip(Board board, Ship ship) {
	};

}
