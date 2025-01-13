package com.miro.Laivanupotus.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.miro.Laivanupotus.service.CustomUserDetailsService;
import com.miro.Laivanupotus.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	    throws ServletException, IOException {

	String token = extractTokenFromHeader(request);

	if (token != null && tokenService.validateToken(token)) {

	    String username = tokenService.getUserNameFromToken(token);
	    System.out.println("Username from token: " + username);
	    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	    System.out.println("User details: " + userDetails);
	    // Create auth token
	    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
		    null, userDetails.getAuthorities());
	    System.out.println("Authentication: " + authentication);
	    // Set the authentication on the security context
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	;

	filterChain.doFilter(request, response);

    }

    private String extractTokenFromHeader(HttpServletRequest req) {
	String bearerToken = req.getHeader("Authorization");
	if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
	    return bearerToken.substring(7);
	}
	;

	return null;
    };

}
