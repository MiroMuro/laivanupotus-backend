package com.miro.Laivanupotus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// Memory based message broker to send messages to clients
		// Clients will subscribe to these destinations to receive updates.
		registry.enableSimpleBroker("/topic");
		// Prefix for messages FROM clients
		registry.setApplicationDestinationPrefixes("/app");
	};

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws-battleship")
		.setAllowedOrigins("http://localhost:5173").withSockJS();
	};

}
