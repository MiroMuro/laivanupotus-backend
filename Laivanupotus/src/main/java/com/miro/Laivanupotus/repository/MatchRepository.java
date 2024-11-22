package com.miro.Laivanupotus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miro.Laivanupotus.model.Match;
import com.miro.Laivanupotus.model.Match.GameStatus;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

	List<Match> findByStatus(GameStatus waitingForPlayer);

	@Override
	Optional<Match> findById(Long matchId);

}
