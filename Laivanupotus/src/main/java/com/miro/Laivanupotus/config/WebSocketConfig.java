package com.miro.Laivanupotus.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.service.CustomUserDetailsService;
import com.miro.Laivanupotus.service.TokenService;
@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    AuthorizationManager<Message<?>> authorizationManager(
	    MessageMatcherDelegatingAuthorizationManager.Builder messages) {
	messages.simpDestMatchers("/user/queue/errors").permitAll().simpDestMatchers("/ws-battleship/**").permitAll()
	.simpDestMatchers("/admin/**").hasRole("ADMIN").anyMessage().authenticated();
	return messages.build();
    }

    // Bye bye csrf-token. You will be implemented some day.
    @Bean
    public ChannelInterceptor csrfChannelInterceptor() {
	return new ChannelInterceptor() {
	    @Override
	    public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
		    List<String> authorization = accessor.getNativeHeader("Authorization");

		    if (authorization != null && !authorization.isEmpty()) {
			String token = authorization.get(0).replace("Bearer ", "");

			if (tokenService.validateToken(token)) {
			    String username = tokenService.getUserNameFromToken(token);
			    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				    userDetails, null, userDetails.getAuthorities());
			    accessor.setUser(new CustomPrincipal((Player) auth.getPrincipal()));
			    // Populate SecurityContextHolder
			    SecurityContextHolder.getContext().setAuthentication(auth);
			}

		    }
		}

		return message;
	    }
	};
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
	registry.enableSimpleBroker("/topic", "/queue");
	registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
	registry.addEndpoint("/ws-battleship").setAllowedOrigins("*");
    }
}

