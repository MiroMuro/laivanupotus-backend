package com.miro.Laivanupotus.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
				UsernamePasswordAuthenticationFilter.class)
		.cors(cors -> cors
				.configurationSource(corsConfigurationSource()));

		return http.build();
	};

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration
		.setAllowedOrigins(Arrays
				.asList("http://localhost:5173"));
		configuration
		.setAllowedMethods(Arrays
				.asList("GET", "POST", "OPTIONS"));
		configuration
		.setAllowedHeaders(Arrays
				.asList("*"));
		configuration
				.setExposedHeaders(Arrays
						.asList("Authorization"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source
		.registerCorsConfiguration("/api/**", configuration);
		return source;
	}
}

