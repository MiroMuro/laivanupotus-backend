package com.miro.Laivanupotus.serviceImp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.dto.LoginRequestDto;
import com.miro.Laivanupotus.dto.NotOwnUserProfileDto;
import com.miro.Laivanupotus.dto.OwnUserProfileDto;
import com.miro.Laivanupotus.dto.UserDto;
import com.miro.Laivanupotus.exceptions.AuthenticationFailedException;
import com.miro.Laivanupotus.exceptions.InvalidPasswordException;
import com.miro.Laivanupotus.exceptions.UserNotFoundException;
import com.miro.Laivanupotus.interfaces.UserProfileDto;
import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.repository.UserRepository;
import com.miro.Laivanupotus.service.TokenService;
import com.miro.Laivanupotus.service.UserService;
import com.miro.Laivanupotus.utils.UserAuthenticator;
import com.miro.Laivanupotus.utils.UserMapper;

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
    public Optional<Player> findById(Long userId) {
	// TODO Auto-generated method stub
	return userRepository.findById(userId);
    }

    @Override
    public Player registerUser(Player user) {
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
    public ResponseEntity<OwnUserProfileDto> loginUser(LoginRequestDto loginDto) throws AuthenticationFailedException {

	String userName = loginDto.getUserName();
	String password = loginDto.getPassword();

	try {

	    if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
		throw new IllegalArgumentException("Username and password cannot be empty");
	    }

	    Player userToLogin = userRepository.findByUserName(userName)
		    .orElseThrow(() -> new UserNotFoundException("User not found" + userName));

	    if (!passwordEncoder.matches(password, userToLogin.getPassword())) {
		throw new InvalidPasswordException("Invalid password for user " + userName);
	    }
	    ;

	    Authentication auth = userAuthenticator.attemptAuthentication(userName, password);

	    // Mistä vitusta tää tulee? Ja olis pitäny tietää.
	    SecurityContextHolder.getContext().setAuthentication(auth);

	    String loginAuthToken = tokenService.generateToken(auth);

	    HttpHeaders authHeader = createAuthHeadersWithToken(loginAuthToken);

	    OwnUserProfileDto userProfileLoginResponse = UserMapper.userToOwnUserProfileDto(userToLogin);
	    try {
		userToLogin.setLastLogin(LocalDateTime.now());
		userRepository.save(userToLogin);
	    } catch (DataAccessException e) {
		System.out.println("Failed to update last login time for user: {}" + userName + e);
	    }
	    ;

	    return ResponseEntity.ok().headers(authHeader).body(userProfileLoginResponse);
	} catch (AuthenticationException e) {
	    throw new AuthenticationFailedException("Authentication failed for user: " + userName);
	} catch (DataAccessException e) {
	    throw new AuthenticationFailedException("System error during authentication" + e);
	}
    }

    HttpHeaders createAuthHeadersWithToken(String token) {
	HttpHeaders authHeader = new HttpHeaders();
	authHeader.add("Authorization", "Bearer " + token);
	return authHeader;
    }

    @Override
    public void logoutUser(Player user) {

    }

    public void updateUserStats(Player user, boolean won) {
	user.setTotalGames(user.getTotalGames() + 1);
	if (won) {
	    user.setGamesWon(user.getGamesWon() + 1);
	} else {
	    user.setGamesLost(user.getGamesLost() + 1);
	}

	userRepository.save(user);
    }

    @Override
    public Optional<Player> findByUsername(String username) {

	return userRepository.findByUserName(username);
    }

    @Override
    public UserProfileDto findUserProfile(Long userId, String authHeader) {

	Player user = userRepository.findById(userId)
		.orElseThrow(() -> new UserNotFoundException("User not with ID: " + userId + " not found!"));

	String authToken = extractTokenFromHeader(authHeader);

	boolean isUsersOwnProfile = isUsersOwnProfile(authToken, user.getUserName());

	// Info depends on if the client is queryings its own profile.
	if (isUsersOwnProfile) {
	    return UserMapper.userToOwnUserProfileDto(user);
	}
	;

	return UserMapper.userToNotOwnUserProfileDto(user);

    };

    @Override
    public UserDto userToDto(Player user) {
	UserDto userDto = new UserDto(user.getUserName(), user.getEmail());
	return userDto;
    }

    public boolean isUsersOwnProfile(String token, String userName) {

	System.out.println("The token in isUsersOwnProfile: " + token);

	String userNameFromToken = tokenService.getUserNameFromToken(token);

	return userNameFromToken.contentEquals(userName) ? true : false;
    }

    public static String extractTokenFromHeader(String AuthHeader) {

	if (AuthHeader != null && AuthHeader.startsWith("Bearer ")) {
	    return AuthHeader.substring(7);
	}
	;

	return null;
    };

    @Override
    public ResponseEntity<List<NotOwnUserProfileDto>> findAllUsersForLeaderboard() {
	List<Player> allUsers = userRepository.findAll();
	List<NotOwnUserProfileDto> allDtoUsers = UserMapper.usersToNotOwnUserProfileDto(allUsers);
	return ResponseEntity.ok(allDtoUsers);

    }

};
