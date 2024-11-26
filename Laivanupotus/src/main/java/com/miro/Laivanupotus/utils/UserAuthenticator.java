package com.miro.Laivanupotus.utils;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.dto.LoginRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAuthenticator {
	private final AuthenticationManager authManager;
	public Authentication attemptAuthentication(LoginRequestDto loginReqDto) throws AuthenticationException {
		String username = loginReqDto.getUserName();
		String password = loginReqDto.getPassword();

		Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
		return authManager.authenticate(auth);
	}
}
