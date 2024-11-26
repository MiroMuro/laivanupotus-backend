package com.miro.Laivanupotus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miro.Laivanupotus.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserName(String username);

	@SuppressWarnings("unchecked")
	@Override
	User save(User user);

	@Override
	Optional<User> findById(Long userId);

}
