package com.miro.Laivanupotus.serviceImp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.Enums.GameStatus;
import com.miro.Laivanupotus.dto.ActiveMatchResponseDto;
import com.miro.Laivanupotus.dto.AvailableMatchResponseDto;
import com.miro.Laivanupotus.dto.WebSocketActiveMatchResponseDto;
import com.miro.Laivanupotus.exceptions.OwnGameJoinException;
import com.miro.Laivanupotus.model.Board;
import com.miro.Laivanupotus.model.Coordinate;
import com.miro.Laivanupotus.model.Match;
import com.miro.Laivanupotus.model.Move;
import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.model.Ship;
import com.miro.Laivanupotus.repository.MatchRepository;
import com.miro.Laivanupotus.service.GameService;
import com.miro.Laivanupotus.utils.MatchMapper;
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

	// WebSocketActiveMatchResponseDto message =
	// WebSocketActiveMatchResponseDto.builder()
	// .message("User " + player.getUserName() + " has joined the
	// game!").status(true).build();
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

	Board board = playerId.equals(match.getPlayer1().getId()) ? match.getPlayer1Board() : match.getPlayer2Board();

	for (Ship ship : ships) {
	    if (!board.isValidPlacement(ship)) {
		throw new RuntimeException(
			"Invalid ship placement! Check your " + ship.getType().toString() + " placement.");
	    }
	    ;
	    board.getShips().add(ship);
	    System.out.println("");
	    updateBoardState(board, ship);
	}
	System.out.println("THe board state is: " + board.getBoardState());
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

	webSocketHandler.notifyMoveMade(matchId, move);

	return move;

    }

    @Override
    public ActiveMatchResponseDto createMatch(Player player) {
	Match newMatch = new Match();
	newMatch.setPlayer1(player);
	newMatch.setStatus(GameStatus.WAITING_FOR_PLAYER);
	newMatch.setStartTime(LocalDateTime.now());
	newMatch.setUpdatedAt(LocalDateTime.now());

	// Initialize the board for player 1.
	Board player1Board = new Board();
	player1Board.setBoardState(".".repeat(100));
	newMatch.setPlayer1Board(player1Board);

	matchRepository.save(newMatch);

	ActiveMatchResponseDto matchDto = MatchMapper.matchToActiveMatchResponseDto(newMatch);

	return matchDto;
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

    private boolean isHit(Board targetBoard, Move move) {

	// S = ship, H = hit, M = miss, . = empty
	char[][] board = convertStringToBoard(targetBoard.getBoardState());

	if (board[move.getY()][move.getX()] == 'H') {
	    return false;
	}
	;

	// Check if the shot that hit sunk any ships.
	if (board[move.getY()][move.getX()] == 'S') {
	    board[move.getY()][move.getX()] = 'H';
	    for (Ship ship : targetBoard.getShips()) {
		ship.setSunk(targetBoard.isShipSunk(ship));
	    }
	    targetBoard.setBoardState(convertBoardToString(board));
	    printBoardToConsole(board);
	    return true;
	}
	;

	board[move.getY()][move.getX()] = 'M';
	targetBoard.setBoardState(convertBoardToString(board));
	printBoardToConsole(board);
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

	boolean result = false;

	if (!match.isPresent()) {
	    return result;
	}

	if (match.get().getPlayer1().getId().equals(playerId) || match.get().getPlayer2().getId().equals(playerId)) {
	    result = true;
	}

	return result;
    }

}
