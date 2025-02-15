package com.miro.Laivanupotus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miro.Laivanupotus.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

	List<Match> findByStatus(
			com.miro.Laivanupotus.Enums.GameStatus waitingForPlayer);
	
	Optional<Match> findByIdAndStatus(Long matchId, com.miro.Laivanupotus.Enums.GameStatus gameStatus);
	
	@Override
	Optional<Match> findById(Long matchId);

}
