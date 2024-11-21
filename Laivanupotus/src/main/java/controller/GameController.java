package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import model.Match;
import model.Move;
import model.Ship;
import model.User;
import service.GameService;
import service.UserService;


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

	public GameController(GameService gameService, UserService userService) {
		this.gameService = gameService;
		this.userService = userService;
	};

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
	public ResponseEntity<List<Match>> getMathcesWaitingForSecondPlayer() {
		List<Match> availableMatches = gameService.findAvailableMatches();
		return ResponseEntity.ok(availableMatches);
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
