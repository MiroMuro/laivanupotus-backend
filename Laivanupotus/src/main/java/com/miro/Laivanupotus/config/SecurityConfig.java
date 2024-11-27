package com.miro.Laivanupotus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.miro.Laivanupotus.service.TokenService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UserDetailsService userDetailsService;
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		//CSRF not required in token-based authentication
		http.csrf(csrf -> csrf.disable())
		//Set session managment to stateless
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> auth.requestMatchers("/api/user/register", "/api/user/login").permitAll()
				.anyRequest().authenticated()
				)
		//Custom JWT filter before standard authentication filter
				.addFilterBefore(
				new JwtAuthenticationFilter(tokenService,
						userDetailsService),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	};
}

