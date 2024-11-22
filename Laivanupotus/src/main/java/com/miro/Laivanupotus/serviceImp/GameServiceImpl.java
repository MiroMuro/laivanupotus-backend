package com.miro.Laivanupotus.serviceImp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.model.Board;
import com.miro.Laivanupotus.model.Match;
import com.miro.Laivanupotus.model.Move;
import com.miro.Laivanupotus.model.Ship;
import com.miro.Laivanupotus.model.User;
import com.miro.Laivanupotus.model.Match.GameStatus;
import com.miro.Laivanupotus.repository.MatchRepository;
import com.miro.Laivanupotus.service.GameService;

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

		Board board = playerId.equals(match.getPlayer1().getId()) ? match.getPlayer1Board() : match.getPlayer2Board();

		for (Ship ship : ships) {
			if (!board.isValidPlacement(ship)) {
				throw new RuntimeException("Invalid ship placement");
			}
			;
			board.getShips().add(ship);
			updateBoardState(board, ship);
		}

		// Check if both players have placed their ships
		if (!match.getPlayer1Board().getShips().isEmpty() && !match.getPlayer2Board().getShips().isEmpty()) {
			match.getStatus();
			match.setStatus(GameStatus.IN_PROGRESS);
			match.setCurrentTurnPlayerId(match.getPlayer1().getId());
		}
		return matchRepository.save(match);
	}

	// playerId is the player who made the move.
	@Override
	public Move makeMove(Long matchId, Long playerId, Move move) {
		Match match = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match not found!"));

		if (match.getStatus() != GameStatus.IN_PROGRESS) {
			throw new RuntimeException("Match is not in progress!");
		}

		if (!playerId.equals(match.getCurrentTurnPlayerId())) {
			throw new RuntimeException("It's not your turn!");

		}

		// Determine which board to target
		Board boardToTarget = playerId.equals(match.getPlayer1().getId()) ? match.getPlayer2Board()
				: match.getPlayer1Board();

		boardToTarget.getMoves().add(move);
		Boolean isHit = isHit(boardToTarget, move);
		move.setHit(isHit);
		move.setPlayerBehindTheMoveId(playerId);

		updateMatchState(match, boardToTarget, playerId);

		matchRepository.save(match);

		return move;

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

	private void updateBoardState(Board targetBoard, Ship ship) {
		char[][] board = convertStringToBoard(targetBoard.getBoardState());
		int length = ship.getType().getLength();

		for (int i = 0; i < length; i++) {
			int x = ship.getX() + (ship.isVertical() ? 0 : i);
			int y = ship.getY() + (ship.isVertical() ? i : 0);
			board[x][y] = 'S';
		}

		System.out.println("board: " + board);

		targetBoard.setBoardState(convertBoardToString(board));
	};

	private boolean isHit(Board targetBoard, Move move) {

		// S = ship, H = hit, M = miss, . = empty
		char[][] board = convertStringToBoard(targetBoard.getBoardState());

		if (board[move.getX()][move.getY()] == 'H') {
			return false;
		}
		;

		// Check if the shot that hit sunk any ships.
		if (board[move.getX()][move.getY()] == 'S') {
			board[move.getX()][move.getY()] = 'H';
			for(Ship ship : targetBoard.getShips()) {
				ship.setSunk(targetBoard.isShipSunk(ship));
				return true;
			}
		}
		;

		board[move.getX()][move.getY()] = 'M';
		System.out.println("board after move: " + board);
		return false;
	};

	// The userId here is the player who made the last move.
	private void updateMatchState(Match match, Board targetBoard, Long userId) {
		// Check if game is over
		if (isGameOver(targetBoard)) {
			match.setEndedAt(LocalDateTime.now());
			match.setUpdatedAt(LocalDateTime.now());
			match.setStatus(GameStatus.FINISHED);
			return;
		}
		;

		match.setCurrentTurnPlayerId(
				userId.equals(match.getPlayer1().getId()) ? match.getPlayer2().getId() : match.getPlayer1().getId());

		match.setUpdatedAt(LocalDateTime.now());

	};

	private char[][] convertStringToBoard(String boardState) {
		char[][] board = new char[10][10];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j] = boardState.charAt(i * 10 + j);
			}
		}

		return board;
	};

	private String convertBoardToString(char[][] board) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				sb.append(board[i][j]);
			}
		}

		return sb.toString();

	};

	private boolean isGameOver(Board board) {
		// Check if all ships are sunk
		return board.getShips().stream().allMatch(ship -> board.isShipSunk(ship));
	};



}
