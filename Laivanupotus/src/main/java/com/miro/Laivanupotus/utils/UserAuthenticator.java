package com.miro.Laivanupotus.utils;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAuthenticator {
    private final AuthenticationManager authManager;

    public Authentication attemptAuthentication(String userName, String password) throws AuthenticationException {
	// String username = loginReqDto.getUserName();
	// String password = loginReqDto.getPassword();

	Authentication auth = new UsernamePasswordAuthenticationToken(userName, password);
	return authManager.authenticate(auth);
    }

    public static void checkUserRoles() {
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	if (authentication != null) {
	    System.out.println("User: " + authentication.getName());
	    System.out.println("Roles: ");
	    for (GrantedAuthority authority : authentication.getAuthorities()) {
		System.out.println(authority.getAuthority());
	    }
	} else {
	    System.out.println("No authenticated user found.");
	}
    }
}
