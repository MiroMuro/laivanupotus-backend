package com.miro.Laivanupotus.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.miro.Laivanupotus.model.User;
import com.miro.Laivanupotus.service.UserService;

@Configuration
public class StartupConfig {

	@Profile("dev")
	@Bean
	public CommandLineRunner cmdRunner(UserService userService) {
		return args -> {
			System.out.println("Initializing database with test data in dev environment");
			if (userService.findByUsername("player1").isEmpty()) {
				User player1 = new User();
				player1.setUsername("player1");
				player1.setPassword("password123");
				player1.setEmail("player1@test.com");
				userService.registerUser(player1);
			}
			if (userService.findByUsername("player2").isEmpty()) {
				User player2 = new User();
				player2.setUsername("player2");
				player2.setPassword("password123");
				player2.setEmail("player2@test.com");
				userService.registerUser(player2);
			}
		};
	};
}
