package com.miro.Laivanupotus.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Service
public class TokenService {
	@Value("${jwt.secret}")
	private String jwtSecret;

	private final long expirationTime = 86400000;

	private SecretKey key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

	};

	public String generateToken(Authentication authentication) {
		return Jwts.builder().subject(authentication.getName()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(key)
				.compact();
	};
}
