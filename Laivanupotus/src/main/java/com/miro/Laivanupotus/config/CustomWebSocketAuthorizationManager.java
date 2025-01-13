package com.miro.Laivanupotus.config;

import java.util.function.Supplier;

import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.messaging.util.matcher.SimpDestinationMessageMatcher;

public class CustomWebSocketAuthorizationManager implements AuthorizationManager<MessageAuthorizationContext<?>> {

    private final SimpDestinationMessageMatcher matcher;
    private final String requiredRole;

    public CustomWebSocketAuthorizationManager(String destinationPattern, String requiredRole) {
	this.matcher = new SimpDestinationMessageMatcher(destinationPattern);
	this.requiredRole = requiredRole;
    }



    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
	    MessageAuthorizationContext<?> context) {
	Message<?> message = context.getMessage();

	// Check if the message matches the destination pattern
	if (matcher.matches(message)) {
	    // If not authenticated, deny access
	    if (authentication == null || !((Authentication) authentication).isAuthenticated()) {
		return new AuthorizationDecision(false);
	    }

	    // Check if the user has the required role
	    boolean hasRole = ((Authentication) authentication).getAuthorities().stream()
		    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(requiredRole));

	    return new AuthorizationDecision(hasRole);
	}

	// If the message doesn't match, skip authorization (null means no decision)
	return null;
    }
}