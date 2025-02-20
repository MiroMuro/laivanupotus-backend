package com.miro.Laivanupotus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.miro.Laivanupotus.Enums.GameStatus;
import com.miro.Laivanupotus.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

	List<Match> findByStatus(
			com.miro.Laivanupotus.Enums.GameStatus waitingForPlayer);
	
	@Query("SELECT m FROM Match m where "+
	"m.id = :matchId AND "+
			"m.player1.id = :playerId OR m.player2.id = :playerId")
	Optional<Match> findActiveMatchByMatchIdAndPlayerId(@Param("matchId")Long matchId, @Param("playerId")Long playerId);
	
	@Query("SELECT COUNT(m) > 0 FROM Match m where "+
		   "(m.player1.id = :playerId OR m.player2.id = :playerId) "+
			"AND m.status != :gameStatus")
	boolean hasUnfinishedGames(@Param("playerId") Long playerId, @Param("gameStatus") GameStatus gameStatus);
	
	@Override
	Optional<Match> findById(Long matchId);

}
