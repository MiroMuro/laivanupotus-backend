package com.miro.Laivanupotus.serviceImp;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.dto.LoginRequestDto;
import com.miro.Laivanupotus.dto.UserDto;
import com.miro.Laivanupotus.model.User;
import com.miro.Laivanupotus.repository.UserRepository;
import com.miro.Laivanupotus.service.TokenService;
import com.miro.Laivanupotus.service.UserService;
import com.miro.Laivanupotus.utils.UserAuthenticator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final TokenService tokenService;
	private final UserAuthenticator userAuthenticator;
	private final BCryptPasswordEncoder passwordEncoder;


	@Override
	public Optional<User> findById(Long userId) {
		// TODO Auto-generated method stub
		return userRepository.findById(userId);
	}

	@Override
	public User registerUser(User user) {
		if (userRepository.findByUserName(user.getUserName()).isPresent()) {
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
	public ResponseEntity<String> loginUser(LoginRequestDto loginDto) {

		String userName = loginDto.getUserName();
		String password = loginDto.getPassword();

		System.out.println("THE UUSERNAME IN QUESTION: " + userName);

		User userToLogin = userRepository.findByUserName(userName)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(password, userToLogin.getPassword())) {
			throw new RuntimeException("Invalid password");
		}
		;

		ResponseEntity<String> loginResponse = getLoginAuthResponse(userName, password);

		userToLogin.setLastLogin(LocalDateTime.now());
		userRepository.save(userToLogin);

		return loginResponse;
	}

	public ResponseEntity<String> getLoginAuthResponse(String userName, String password) {
		try {
			Authentication auth = userAuthenticator.attemptAuthentication(userName, password);
			String loginAuthToken = tokenService.generateToken(auth);

			HttpHeaders authHeader = createAuthHeadersWithToken(loginAuthToken);

			return ResponseEntity.ok().headers(authHeader).body("Login succesful!");

		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}

	};

	public HttpHeaders createAuthHeadersWithToken(String token) {
		HttpHeaders authHeader = new HttpHeaders();
		authHeader.add("Authorization", "Bearer " + token);
		return authHeader;
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
		return userRepository.findByUserName(username);
	}

	@Override
	public UserDto userToDto(User user) {
		UserDto userDto = new UserDto(user.getUserName(), user.getEmail());
		return userDto;
	}



};
