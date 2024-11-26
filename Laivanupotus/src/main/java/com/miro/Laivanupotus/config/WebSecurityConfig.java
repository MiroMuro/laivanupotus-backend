package com.miro.Laivanupotus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// This class is used to configure the password encoder for the UserServiceImpl.
@Configuration
public class WebSecurityConfig {
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfig)
			throws Exception {
		return authenticationConfig.getAuthenticationManager();

	};
}
