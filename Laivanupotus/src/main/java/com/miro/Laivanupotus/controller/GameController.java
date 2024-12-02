package com.miro.Laivanupotus.controller;

import java.util.List;
import java.util.Map;

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
import com.miro.Laivanupotus.model.Ship;
import com.miro.Laivanupotus.model.User;
import com.miro.Laivanupotus.service.GameService;
import com.miro.Laivanupotus.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/game")
/*
 * Generates a constructor with required arguments.
 * Required arguments are final fields and fields with constraints such as {@code @NonNull}.
 */
@RequiredArgsConstructor
public class GameController {
	private final GameService gameService;
	private final UserService userService;



	@PostMapping("/create")
	public ResponseEntity<ActiveMatchResponseDto> createGame(
			@RequestParam Long userId) {
		User player = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));

		ActiveMatchResponseDto newMatchDto = gameService
				.createMatch(player);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(newMatchDto);
	};

	@PostMapping("/{matchId}/join")
	public ResponseEntity<ActiveMatchResponseDto> joinGame(
			@RequestParam Long userId,
			@PathVariable Long matchId) {
		User player = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));

		ActiveMatchResponseDto matchWithPlayersJoined = gameService
				.joinMatch(matchId, player);

		return ResponseEntity.ok(matchWithPlayersJoined);
	};

	@GetMapping("/available")
	public ResponseEntity<List<AvailableMatchResponseDto>> getMathcesWaitingForSecondPlayer() {
		List<AvailableMatchResponseDto> availableMatches = gameService
				.findAvailableMatches();

		return ResponseEntity
				.ok(availableMatches);
	};

	@GetMapping("/{matchId}")
	public ResponseEntity<Match> getMatchDetails(@PathVariable Long matchId) {
		Match match = gameService.getMatchById(matchId).orElseThrow(() -> new RuntimeException("Match not found!"));

		return ResponseEntity.ok(match);
	};

	@PostMapping("/{matchId}/place-ships")
	public ResponseEntity<Match> placeShips(@PathVariable Long matchId,
			@RequestParam Long userId,
			@RequestBody Map<String, List<Ship>> payload) {
		List<Ship> ships = payload
				.get("ships");

		Match updatedMatchWithShips = gameService
				.placeShips(matchId, userId, ships);
		// return ResponseEntity.ok(updatedMatchWithShips);

		return ResponseEntity
				.ok(updatedMatchWithShips);
	};

	@PostMapping("/{matchId}/make-move")
	public ResponseEntity<Move> makeMove(@PathVariable Long matchId,
			@RequestParam Long userId,
			@RequestBody Move move) {
		Move resultMove = gameService
				.makeMove(matchId, userId, move);

		return ResponseEntity.ok(resultMove);
	};

}
