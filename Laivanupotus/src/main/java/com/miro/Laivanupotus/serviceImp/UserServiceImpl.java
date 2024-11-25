package com.miro.Laivanupotus.serviceImp;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.dto.UserDto;
import com.miro.Laivanupotus.model.User;
import com.miro.Laivanupotus.repository.UserRepository;
import com.miro.Laivanupotus.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	private final BCryptPasswordEncoder passwordEncoder;

	@Override
	public Optional<User> findById(Long userId) {
		// TODO Auto-generated method stub
		return userRepository.findById(userId);
	}

	@Override
	public User registerUser(User user) {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("Username already exists");
		}
		user.setTotalGames(0);
		user.setGamesWon(0);
		user.setGamesLost(0);
		user.setCreatedAt(LocalDateTime.now());
		user.setLastLogin(LocalDateTime.now());

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public User loginUser(String userName, String password) {

		User userToLogin = userRepository.findByUsername(userName)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(password, userToLogin.getPassword())) {
			throw new RuntimeException("Invalid password");
		}
		;

		userToLogin.setLastLogin(LocalDateTime.now());
		// TODO Auto-generated method stub
		return userRepository.save(userToLogin);
	}

	@Override
	public void logoutUser(User user) {
		// TODO Auto-generated method stub

	}

	public void updateUserStats(User user, boolean won) {
		user.setTotalGames(user.getTotalGames()+1);
		if(won) {
			user.setGamesWon(user.getGamesWon()+1);
		} else {
			user.setGamesLost(user.getGamesLost()+1);
		}

		userRepository.save(user);
	}

	@Override
	public Optional<User> findByUsername(String username) {
		// TODO Auto-generated method stub
		return userRepository.findByUsername(username);
	}

	@Override
	public UserDto userToDto(User user) {
		UserDto userDto = new UserDto(user.getUsername(), user.getEmail());
		return userDto;
	}
};
