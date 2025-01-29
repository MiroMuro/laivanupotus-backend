package com.miro.Laivanupotus.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miro.Laivanupotus.dto.ActiveMatchResponseDto;
import com.miro.Laivanupotus.dto.AvailableMatchResponseDto;
import com.miro.Laivanupotus.model.Match;
import com.miro.Laivanupotus.model.Move;
import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.model.Ship;
import com.miro.Laivanupotus.service.GameService;
import com.miro.Laivanupotus.service.UserService;
import com.miro.Laivanupotus.utils.UserAuthenticator;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ActiveMatchResponseDto> createGame(@RequestParam Long userId) {
	Player player = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));

	ActiveMatchResponseDto newMatchDto = gameService.createMatch(player);

	return ResponseEntity.status(HttpStatus.CREATED).body(newMatchDto);
    };

    @PostMapping("/{matchId}/join")
    public ResponseEntity<ActiveMatchResponseDto> joinGame(@RequestParam Long userId, @PathVariable Long matchId) {
	Player player = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));

	ActiveMatchResponseDto matchWithPlayersJoined = gameService.joinMatch(matchId, player);

	return ResponseEntity.ok(matchWithPlayersJoined);
    };

    @PostMapping("/{matchId}/authorize")
    public ResponseEntity<String> authorizeGame(@RequestParam Long userId, @PathVariable Long matchId) {

	ResponseEntity<String> responseMessage;

	boolean matchAuthorizationResult = gameService.authorizeMatch(matchId, userId);

	if (!matchAuthorizationResult) {
	    return responseMessage = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		    .body("You are not authorized to play this match.");
	}

	return responseMessage = ResponseEntity.ok("You are authorized to play this match.");

    };

    @GetMapping("/available")
    public ResponseEntity<List<AvailableMatchResponseDto>> getMathcesWaitingForSecondPlayer() {
	logger.info("Getting available matches");
	UserAuthenticator.checkUserRoles();
	System.out.println("Getting available matches");
	List<AvailableMatchResponseDto> availableMatches = gameService.findAvailableMatches();

	return ResponseEntity.ok(availableMatches);
    };

    @GetMapping("/{matchId}")
    public ResponseEntity<Match> getMatchDetails(@PathVariable Long matchId) {
	Match match = gameService.getMatchById(matchId).orElseThrow(() -> new RuntimeException("Match not found!"));

	return ResponseEntity.ok(match);
    };

    @PostMapping("/{matchId}/place-ships")
    public ResponseEntity<Match> placeShips(@PathVariable Long matchId, @RequestParam Long userId,
	    @RequestBody List<Ship> payload) {
	System.out.println("In place ships controller method");
	System.out.println("The payload is: " + payload);

	List<Ship> ships = payload;

	System.out.println("The ships are: " + ships);

	Match updatedMatchWithShips = gameService.placeShips(matchId, userId, ships);
	// return ResponseEntity.ok(updatedMatchWithShips);

	return ResponseEntity.ok(updatedMatchWithShips);
    };

    @PostMapping("/{matchId}/make-move")
    public ResponseEntity<Move> makeMove(@PathVariable Long matchId, @RequestParam Long userId,
	    @RequestBody Move move) {
	Move resultMove = gameService.makeMove(matchId, userId, move);

	return ResponseEntity.ok(resultMove);
    };
    // Todo. Implement Leave match functionality.
    // @PostMapping("/{matchId}/leave")
}
