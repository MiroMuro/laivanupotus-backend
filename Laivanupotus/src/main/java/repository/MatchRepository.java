package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import model.Match;
import model.Match.GameStatus;

public interface MatchRepository extends JpaRepository<Match, Long> {

	List<Match> findByStatus(GameStatus waitingForPlayer);

}
