package com.miro.Laivanupotus.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miro.Laivanupotus.dto.MatchResponseDto;
import com.miro.Laivanupotus.model.Match;
import com.miro.Laivanupotus.model.Move;
import com.miro.Laivanupotus.model.Ship;
import com.miro.Laivanupotus.model.User;
import com.miro.Laivanupotus.service.GameService;
import com.miro.Laivanupotus.service.UserService;
import com.miro.Laivanupotus.utils.MatchMapper;

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



	//New game creation
	@PostMapping("/create")
	public ResponseEntity<Match> createGame(@RequestParam Long userId) {
		User player = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));

		Match newMatch = gameService.createMatch(player);

		return ResponseEntity.status(HttpStatus.CREATED).body(newMatch);
	};

	@PostMapping("/{matchId}/join")
	public ResponseEntity<Match> joinGame(@RequestParam Long userId, @PathVariable Long matchId) {
		User player = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));

		Match matchWithPlayersJoined = gameService.joinMatch(matchId, player);

		return ResponseEntity.ok(matchWithPlayersJoined);
	};

	@GetMapping("/available")
	public ResponseEntity<List<MatchResponseDto>> getMathcesWaitingForSecondPlayer() {
		List<Match> availableMatches = gameService.findAvailableMatches();
		List<MatchResponseDto> availableMatchesDtos = MatchMapper
				.matchesToDto(availableMatches);
		return ResponseEntity.ok(availableMatchesDtos);
	};

	@GetMapping("/{matchId}")
	public ResponseEntity<Match> getMatchDetails(@PathVariable Long matchId) {
		Match match = gameService.getMatchById(matchId).orElseThrow(() -> new RuntimeException("Match not found!"));

		return ResponseEntity.ok(match);
	};

	@PostMapping("/{matchId}/place-ships")
	public ResponseEntity<Match> placeShips(@PathVariable Long matchId, @RequestParam Long playerId,
			@RequestParam List<Ship> ships) {
		Match updatedMatchWithShips = gameService.placeShips(matchId, playerId, ships);
		return ResponseEntity.ok(updatedMatchWithShips);
	};

	@PostMapping("/{matchId}/make-move")
	public ResponseEntity<Move> makeMove(@PathVariable Long matchId, @RequestParam Long playerId,
			@RequestParam Move move) {
		Move resultMove = gameService.makeMove(matchId, playerId, move);

		return ResponseEntity.ok(resultMove);
	};

}
