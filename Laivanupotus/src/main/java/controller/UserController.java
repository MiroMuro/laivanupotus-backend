package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	@PostMapping("/register")
	public ResponseEntity<User> registerNewUser(@RequestBody User user) {
		User newRegisteredUser = userService.registerUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(newRegisteredUser);
	};

	@PostMapping("/login")
	public ResponseEntity<User> loginUser(@RequestBody User user) {
		User loggedInUser = userService.loginUser(user);
		return ResponseEntity.ok(loggedInUser);
	};

	@PostMapping("/logout")
	public ResponseEntity logOutUser(@RequestBody User user) {
		userService.logoutUser(user);
		return ResponseEntity.ok().build();
	};

	@PostMapping("/{userId")
	public ResponseEntity<User> getUserProfile(@PathVariable Long userId) {
		User user = userService.findById(userId).orElseThrow(() -> new RunTimeException("User not found!"));
		return ResponseEntity.ok(user);
	};
}
