package com.miro.Laivanupotus.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.service.UserService;

@Configuration
public class StartupConfig {


    @Profile("dev")
    @Bean
    public CommandLineRunner cmdRunner(UserService userService) {
	return args -> {
	    System.out.println("Initializing database with test data in dev environment");
	    if (userService.findByUsername("player1").isEmpty()) {
		Player player1 = new Player();
		player1.setUserName("player1");
		player1.setPassword("password123");
		player1.setEmail("player1@test.com");
		player1.setRoles("ROLE_USER");
		userService.registerUser(player1);
	    }
	    if (userService.findByUsername("player2").isEmpty()) {
		Player player2 = new Player();
		player2.setUserName("player2");
		player2.setPassword("password123");
		player2.setEmail("player2@test.com");
		player2.setRoles("ROLE_USER");
		userService.registerUser(player2);
	    }
	};
    };

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
	System.out.println("Initializing in-memory test users");
	UserDetails player1 = User.builder().username("player1")
		.password(WebSecurityConfig.passwordEncoder().encode("password123")).roles("USER").build();

	UserDetails player2 = User.builder().username("player2")
		.password(WebSecurityConfig.passwordEncoder().encode("password123")).roles("USER").build();

	return new InMemoryUserDetailsManager(player1, player2);
    }
}
