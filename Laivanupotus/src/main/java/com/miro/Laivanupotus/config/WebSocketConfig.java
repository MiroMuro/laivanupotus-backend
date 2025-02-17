package com.miro.Laivanupotus.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miro.Laivanupotus.model.PlayerConnectionMessage;
import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.service.ConnectionEventService;
import com.miro.Laivanupotus.service.CustomUserDetailsService;
import com.miro.Laivanupotus.service.TokenService;
import com.miro.Laivanupotus.serviceImp.GameServiceImpl;
import com.miro.Laivanupotus.websocket.GameWebSocketHandler;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	private Map<Player, Long> reconnectingPlayers = new ConcurrentHashMap<>();
	
	@Autowired
	private ConnectionEventService connectionEventService;
//    
//    @Autowired
//    private  GameWebSocketHandler webSocketHandler;
	@Bean
	AuthorizationManager<Message<?>> authorizationManager(
			MessageMatcherDelegatingAuthorizationManager.Builder messages) {

		// Error destinations always allowed
		messages.simpDestMatchers("/user/queue/errors").permitAll()
				// Websocket endpoints is permitted for all
				.simpDestMatchers("/ws-battleship/**").permitAll()
				// Subscribe destinations for game topics are allowed only for authenticated
				.simpSubscribeDestMatchers("/topic/game/**").authenticated().simpDestMatchers("/topic/game/**")
				// Send destinations for game topics are allowed only for authenticated
				.authenticated().simpDestMatchers("/app/**").authenticated()
				// Admin endpoints need ADmin role.
				.simpDestMatchers("/admin/**").hasRole("ADMIN").anyMessage().authenticated();

		MessageMatcherDelegatingAuthorizationManager delegate = (MessageMatcherDelegatingAuthorizationManager) messages
				.build();

		return new AuthorizationManager<Message<?>>() {

			@Override
			public AuthorizationDecision check(Supplier<Authentication> authentication, Message<?> message) {

				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					return new AuthorizationDecision(true);
				}

				// For other frames, check if user is authenticated via accessor
				CustomPrincipal user = (CustomPrincipal) accessor.getUser();

				boolean isAuthenticated = (user != null && user instanceof CustomPrincipal);

				if (!isAuthenticated) {
					return new AuthorizationDecision(false);
				}

				// Create an new Authentication object with user credentials for the
				// MessageMatcherDelegatingAuthorizationManager
				String username = user.getName();
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());

				// Delegate to the declarative rules for specific destinations
				return delegate.check(() -> auth, message);
			}
		};

	}

	// Weirdly enough, this functions name "csrfChannelInterceptor cannot be
	// changed.
	// Otherwise the websocket connection will not work.
	@Bean
	public ChannelInterceptor csrfChannelInterceptor() {
		return new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {

				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				
				StompCommand messageStompCommand = accessor.getCommand();
				
				if (StompCommand.CONNECT.equals(messageStompCommand)) {

					
					List<String> authorizationHeader = validateAuthorizationHeaders(accessor);
					
					String AuthtokenFromHeaders = getAuthTokenFromHeaders(authorizationHeader);
					
					boolean tokenIsValid = authTokenIsValid(AuthtokenFromHeaders);
					
					if (tokenIsValid) {
						UserDetails userDetailsFromToken = getUserDetailsFromToken(AuthtokenFromHeaders);
						
						setUserToAccessor(accessor, userDetailsFromToken);
						
						Player user = getPlayerFromAccessor(accessor);
						
						reconnectPlayerIfDisconnected(user);
						
						connectionEventService.publishConnection(user, "Player connected.");
					};
				
				}

				if (StompCommand.SUBSCRIBE.equals(messageStompCommand)) {
					
					String urlDestination = accessor.getDestination();
					
					if (isValidUrl(urlDestination)) {
						return message;
					}

				}

				if (StompCommand.DISCONNECT.equals(messageStompCommand)) {
					Player user = getPlayerFromAccessor(accessor);				
					handlePlayerDisconnect(user);

				}

				if (StompCommand.SEND.equals(messageStompCommand)) {
					System.out.println("The message is: " + message.getPayload());
					System.out.println("Sending message to: " + accessor.getDestination());

				}

				return message;
			}
		};
	}
	
	private void setUserToAccessor(StompHeaderAccessor accessor, UserDetails userDetailsFromToken) {
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				userDetailsFromToken, null, userDetailsFromToken.getAuthorities());
		accessor.setUser(new CustomPrincipal((Player) auth.getPrincipal()));
		// TODO Auto-generated method stub
		
	}

	private UserDetails getUserDetailsFromToken(String token) {
		String username = tokenService.getUserNameFromToken(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return userDetails;
	};
	
	private boolean isValidUrl (String urlDestination) {
		if(urlDestination != null && isValidBattleShipSubscription(urlDestination)) {
			return true;
		}
		return false;
	}
	
	private boolean authTokenIsValid(String token)  {
		
		return tokenService.validateToken(token);
	};
	
	private void reconnectPlayerIfDisconnected(Player user) {
		if (reconnectingPlayers.containsKey(user)) {
			reconnectingPlayers.remove(user);
			connectionEventService.publishReconnection(user,"Player reconnected.");
			return;
		}
		return;
	};
	private String getAuthTokenFromHeaders(List<String> authorization) {
		String token = authorization.get(0).replace("Bearer ", "");
		return token;
	};
	
	private List<String> validateAuthorizationHeaders(StompHeaderAccessor accessor) throws MessageDeliveryException {
		List<String> authorization = accessor.getNativeHeader("Authorization");
		
		if (authorization == null || authorization.isEmpty()) {
			throw new MessageDeliveryException("Authorization header not found.");
		}
		
		return authorization;
	};
	
	private void handlePlayerDisconnect(Player user) {
		reconnectingPlayers.put(user, System.currentTimeMillis());
		connectionEventService.publisDisconnection(user, "Player disconnected.");
		scheduleDisconnectionCheck(user);
	};
	
	private Player getPlayerFromAccessor(StompHeaderAccessor accessor) throws MessageDeliveryException {
		CustomPrincipal principal = (CustomPrincipal) accessor.getUser();
		Player user = principal.getPlayer();
		if(user == null) {
			throw new MessageDeliveryException("User not found in accessor.");
		}
		return user;
	};

	private void scheduleDisconnectionCheck(Player user) {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(() -> {
			if (reconnectingPlayers.containsKey(user)) {
				reconnectingPlayers.remove(user);
				handlePlayerTimeout(user);
			}
			executor.shutdown();
		}, 15, TimeUnit.SECONDS);
	}

	private void handlePlayerTimeout(Player user) {
		System.out.println("User " + user.getUserName() + " timed out.");
		connectionEventService.publishTimeOut(user, "Player  timed out.");
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
	
	private boolean isValidBattleShipSubscription(String urlDestination) {
		if (urlDestination.startsWith("/topic/game")) {
			// TODO: Implement logic to check if player is already in the game.
			return true;
		}
		

		return false;
	};

}
