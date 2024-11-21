package service;

import java.util.Optional;

import model.User;

public interface UserService {
	Optional<User> findById(Long userId);

	User registerUser(User user);

	User loginUser(User user);

	void logoutUser(User user);
}
