package com.miro.Laivanupotus.serviceImp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.Enums.GameStatus;
import com.miro.Laivanupotus.dto.ActiveMatchResponseDto;
import com.miro.Laivanupotus.dto.AvailableMatchResponseDto;
import com.miro.Laivanupotus.dto.IngameUserProfileDto;
import com.miro.Laivanupotus.dto.WebSocketActiveMatchResponseDto;
import com.miro.Laivanupotus.dto.WebSocketGameStatusUpdateResponseDto;
import com.miro.Laivanupotus.dto.WebSocketMoveResponseDto;
import com.miro.Laivanupotus.exceptions.MatchNotFoundException;
import com.miro.Laivanupotus.exceptions.OwnGameJoinException;
import com.miro.Laivanupotus.exceptions.PlayerInActiveMatchException;
import com.miro.Laivanupotus.model.Board;
import com.miro.Laivanupotus.model.Coordinate;
import com.miro.Laivanupotus.model.PlayerConnectionMessage;
import com.miro.Laivanupotus.model.Match;
import com.miro.Laivanupotus.model.Move;
import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.model.Ship;
import com.miro.Laivanupotus.repository.MatchRepository;
import com.miro.Laivanupotus.service.GameService;
import com.miro.Laivanupotus.utils.MatchMapper;
import com.miro.Laivanupotus.utils.UserMapper;
import com.miro.Laivanupotus.websocket.GameWebSocketHandler;

@Service
public class GameServiceImpl implements GameService {

	private final MatchRepository matchRepository;

	private final GameWebSocketHandler webSocketHandler;

	@Autowired
	public GameServiceImpl(MatchRepository matchRepository, GameWebSocketHandler webSocketHandler) {
		this.matchRepository = matchRepository;
		this.webSocketHandler = webSocketHandler;
	}

	@Override
	public List<AvailableMatchResponseDto> findAvailableMatches() {
		System.out.println("Finding available matches");
		List<Match> matches = matchRepository.findByStatus(GameStatus.WAITING_FOR_PLAYER);

		List<AvailableMatchResponseDto> matchDtos = MatchMapper.matchesToDto(matches);

		;

		return matchDtos;
	}

	@Override
	public ActiveMatchResponseDto joinMatch(Long matchId, Player player) {
		Match matchRequiringPlayer2 = matchRepository.findById(matchId)
				.orElseThrow(() -> new RuntimeException("Match not found!"));

		if (matchRequiringPlayer2.getStatus() != GameStatus.WAITING_FOR_PLAYER) {
			throw new RuntimeException("Match is not waiting for a player!");
		}

		if (matchRequiringPlayer2.getPlayer2() != null) {
			throw new RuntimeException("Match is already full!");
		}

		if (matchRequiringPlayer2.getPlayer1().getId().equals(player.getId())) {
			throw new OwnGameJoinException("You cannot join your own match!");
		}

		matchRequiringPlayer2.setPlayer2(player);
		matchRequiringPlayer2.setStatus(GameStatus.PLACING_SHIPS);
		matchRequiringPlayer2.setUpdatedAt(LocalDateTime.now());

		Board player2Board = new Board();
		player2Board.setBoardState(".".repeat(100));
		matchRequiringPlayer2.setPlayer2Board(player2Board);

		matchRepository.save(matchRequiringPlayer2);

		ActiveMatchResponseDto matchDto = MatchMapper.matchToActiveMatchResponseDto(matchRequiringPlayer2);

		WebSocketActiveMatchResponseDto message = createWebSocketMessage(matchDto, player.getUserName());
		webSocketHandler.notifyPlayerJoined(matchId, message);

		return matchDto;
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

		Board boardToPlaceShipsOn = playerId.equals(match.getPlayer1().getId()) ? match.getPlayer1Board()
				: match.getPlayer2Board();

		confirmValidShipPlacement(boardToPlaceShipsOn, ships);

		beginGameIfBothPlayersPlacedShips(match);

		return matchRepository.save(match);
	}

	public void confirmValidShipPlacement(Board board, List<Ship> ships) {
		for (Ship ship : ships) {
			if (!board.isValidPlacement(ship)) {
				throw new RuntimeException(
						"Invalid ship placement! Check your " + ship.getType().toString() + " placement.");
			}
			ship.setBoardId(board.getId());
			board.getShips().add(ship);
			updateBoardState(board, ship);
		}
	}

	private void updateBoardState(Board targetBoard, Ship ship) {
		char[][] board = convertStringToBoard(targetBoard.getBoardState());
		// int length = ship.getType().getLength();
		List<Coordinate> shipCoords = ship.getCoordinates();

		for (int i = 0; i < shipCoords.size(); i++) {
			int x = shipCoords.get(i).getX();
			int y = shipCoords.get(i).getY();
			board[y][x] = 'S';
		}

		printBoardToConsole(board);

		targetBoard.setBoardState(convertBoardToString(board));
	};

	private void beginGameIfBothPlayersPlacedShips(Match match) {
		if (!match.getPlayer1Board().getShips().isEmpty() && !match.getPlayer2Board().getShips().isEmpty()) {
			System.out.println("Both players have placed their ships!");
			Match startedMatch = startMatch(match);

			WebSocketGameStatusUpdateResponseDto gameBeginsMessage = createMatchStartOrEndWebsocketMessage(
					startedMatch);
			webSocketHandler.notifyGameUpdate(startedMatch.getId(), gameBeginsMessage);
		}
	};

	private Match startMatch(Match match) {
		match.getStatus();
		match.setStatus(GameStatus.IN_PROGRESS);
		match.setCurrentTurnPlayerId(match.getPlayer1().getId());
		return match;
	}

	private WebSocketGameStatusUpdateResponseDto createMatchStartOrEndWebsocketMessage(Match match) {
		IngameUserProfileDto player1 = (IngameUserProfileDto) UserMapper.userToIngameUserProfileDto(match.getPlayer1());
		WebSocketGameStatusUpdateResponseDto gameBeginsMessage = WebSocketGameStatusUpdateResponseDto.builder()
				.player(player1).status(match.getStatus()).build();

		return gameBeginsMessage;
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

		if (hasCoordinateAlreadyBeenHit(boardToTarget, move)) {
			throw new RuntimeException("You have already made a move on this coordinate!");
		}

		boardToTarget.getMoves().add(move);

		Boolean isHit = isHit(boardToTarget, move);
		move.setHit(isHit);
		move.setPlayerBehindTheMoveId(playerId);

		String moveResponseMessage = getMoveResponseMessage(move, boardToTarget);

		WebSocketMoveResponseDto message = WebSocketMoveResponseDto.builder().move(move).message(moveResponseMessage)
				.build();

		updateMatchState(match, boardToTarget, playerId);

		matchRepository.save(match);

		webSocketHandler.notifyMoveMade(matchId, message);

		return move;

	}

	private String getMoveResponseMessage(Move move, Board boardToTarget) {
		Boolean isHit = move.isHit();
		if (isHit) {
			return getShipHitMessage(move, boardToTarget);
		}
		return "You missed!";

	}

	private String getShipHitMessage(Move move, Board boardToTarget) {
		Ship hitShip = getShipFromMove(move, boardToTarget);
		Boolean isShipSunk = boardToTarget.isShipSunk(hitShip);

		if (isShipSunk) {
			hitShip.setSunk(true);
			return "Great shot! You sunk the enemy's " + hitShip.getType().toString() + "!";
		} else {
			return "Nice shot! You hit the the enemy!";
		}
	}

	@Override
	public ActiveMatchResponseDto createMatch(Player player) {

		if (playerIsInAnActiveMatch(player)) {
			throw new PlayerInActiveMatchException(
					"You are already in an active match! You cannot play multiple matches at once!");
		}
		;

		Match newMatch = createNewMatch(player);

		Board player1Board = createNewBoard();
		// Initialize the board for player 1.

		newMatch.setPlayer1Board(player1Board);

		matchRepository.save(newMatch);

		ActiveMatchResponseDto matchDto = MatchMapper.matchToActiveMatchResponseDto(newMatch);

		return matchDto;
	}

	private Match createNewMatch(Player player) {
		Match newMatch = new Match();
		newMatch.setPlayer1(player);
		newMatch.setStatus(GameStatus.WAITING_FOR_PLAYER);
		newMatch.setStartTime(LocalDateTime.now());
		newMatch.setUpdatedAt(LocalDateTime.now());

		return newMatch;
	};

	private Board createNewBoard() {
		Board newBoard = new Board();
		newBoard.setBoardState(".".repeat(100));
		return newBoard;
	}

	private WebSocketActiveMatchResponseDto createWebSocketMessage(ActiveMatchResponseDto matchDto,
			String joinedUserName) {
		WebSocketActiveMatchResponseDto message = WebSocketActiveMatchResponseDto.builder().id(matchDto.getId())
				.player1(matchDto.getPlayer1()).player2(matchDto.getPlayer2()).player1Board(matchDto.getPlayer1Board())
				.player2Board(matchDto.getPlayer2Board()).status(matchDto.getStatus())
				.currentTurnPlayerId(matchDto.getCurrentTurnPlayerId()).startTime(matchDto.getStartTime())
				.updatedAt(matchDto.getUpdatedAt()).endedAt(matchDto.getEndedAt())
				.message("User " + joinedUserName + " has joined the game!").messageStatus(true).build();

		return message;
	};

	private boolean isHit(Board targetBoard, Move move) {
		// targetBoard.getMoves().add(move);
		// S = ship, H = hit, M = miss, . = empty
		char[][] board = convertStringToBoard(targetBoard.getBoardState());

		Boolean isHit = false;

		if (board[move.getY()][move.getX()] == 'S') {
			board[move.getY()][move.getX()] = 'H';
//	    for (Ship ship : targetBoard.getShips()) {
//		ship.setSunk(targetBoard.isShipSunk(ship));
//	    }

			isHit = true;

		} else {
			board[move.getY()][move.getX()] = 'M';
		}

		targetBoard.setBoardState(convertBoardToString(board));
		printBoardToConsole(board);
		return isHit;
	};

	private Ship getShipFromMove(Move move, Board targetBoard) {
		Coordinate moveCoord = new Coordinate(move.getX(), move.getY());
		Ship hitShip = targetBoard.getShips().stream().filter(ship -> ship.getCoordinates().contains(moveCoord))
				.findFirst().orElse(null);
		return hitShip;
	}

	private Boolean hasCoordinateAlreadyBeenHit(Board targetBoard, Move move) {
		char[][] board = convertStringToBoard(targetBoard.getBoardState());
		if (board[move.getY()][move.getX()] == 'H') {
			return true;
		}
		;

		return false;
	};

	// The userId here is the player who made the last move.
	private void updateMatchState(Match match, Board targetBoard, Long userId) {
		// Check if game is over
		if (isGameOver(targetBoard)) {
			endMatch(match);
			return;
		}
		;

		match.setCurrentTurnPlayerId(
				userId.equals(match.getPlayer1().getId()) ? match.getPlayer2().getId() : match.getPlayer1().getId());

		match.setUpdatedAt(LocalDateTime.now());

	};

	private void endMatch(Match match) {
		match.setEndedAt(LocalDateTime.now());
		match.setUpdatedAt(LocalDateTime.now());
		match.setStatus(GameStatus.FINISHED);
		WebSocketGameStatusUpdateResponseDto gameEndedMessage = createMatchStartOrEndWebsocketMessage(match);
		webSocketHandler.notifyGameUpdate(match.getId(), gameEndedMessage);
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

	private void printBoardToConsole(char[][] board) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println(); // Move to the next line after each row
		}
	}

	@Override
	public boolean authorizeMatch(Long matchId, Long playerId) {
		Optional<Match> match = getMatchById(matchId);

		System.out.println("The match is: " + match);
		System.out.println("The player id is: " + playerId);
		System.out.println("The match id is: " + matchId);
		boolean result = false;

		if (!match.isPresent()) {
			return result;
		}

		if (match.get().getPlayer1().getId().equals(playerId) || match.get().getPlayer2().getId().equals(playerId)) {
			result = true;
		}

		return result;
	}

	public ActiveMatchResponseDto getActiveMatchByUserIdAndMatchId(Long matchId, Long playerId) {
		

		Match match = matchRepository.findActiveMatchByMatchIdAndPlayerId(matchId, playerId)
				.orElseThrow(() -> new MatchNotFoundException(
						"Match of id " + matchId + ", and with player id : " + playerId + "not found!"));
		ActiveMatchResponseDto matchDto = MatchMapper.matchToActiveMatchResponseDto(match);
		return matchDto;
	}

	@Override
	public void disconnectPlayer(PlayerConnectionMessage disconnectMessage, Long matchId) {
		webSocketHandler.notifyOpponentDisconnect(matchId, disconnectMessage);
	};

	private boolean playerIsInAnActiveMatch(Player player) {

		boolean hasUnfinishedGames = matchRepository.hasUnfinishedGames(player.getId(), GameStatus.FINISHED);
		List<Match> match = matchRepository.findAll();
		System.out.println("All matches: " + match);
		if (hasUnfinishedGames) {
			System.out.println("The player has unfinished games." + player.toString());
			return true;
		} else {
			System.out.println("The player has no unfinished games." + player.toString());
		}

		return false;
	}

}
