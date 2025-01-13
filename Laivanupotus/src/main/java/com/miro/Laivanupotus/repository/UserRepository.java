package com.miro.Laivanupotus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miro.Laivanupotus.model.Player;

@Repository
public interface UserRepository extends JpaRepository<Player, Long> {

	Optional<Player> findByUserName(String username);

	@SuppressWarnings("unchecked")
	@Override
	Player save(Player user);

	@Override
	Optional<Player> findById(Long userId);

}
