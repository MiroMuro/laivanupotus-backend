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
							
							CustomPrincipal principal = (CustomPrincipal) accessor.getUser();
							Player user = principal.getPlayer();
							System.out.println("The user connecting here is:"+user.toString());
							System.out.println("Reconnectingplayers in connect method"+reconnectingPlayers.toString());
							if(reconnectingPlayers.containsKey(user)) {
								System.out.println("User " + principal.getName() + " reconnected to the game.");
								reconnectingPlayers.remove(user);
							}
						}

					}
				}

				if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

					String urlDestination = accessor.getDestination();
					if (urlDestination != null) {
						CustomPrincipal principal = (CustomPrincipal) accessor.getUser();
						if (!isValidBattleShipSubscription(urlDestination, principal)) {
							throw new MessageDeliveryException(
									"Invalid subscription destination url!: " + urlDestination);
						}
						;
					}
				}

				if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
					CustomPrincipal principal = (CustomPrincipal) accessor.getUser();
					if (principal != null) {
						Player user = principal.getPlayer();
						System.out.println("Disconnecting user: " + user.toString());
						//handleStompDisconnect(accessor);
						reconnectingPlayers.put(user, System.currentTimeMillis());
						System.out.println("Reconnectingplayers in connect method"+reconnectingPlayers.toString());
						scheduleDisconnectionCheck(user);
					}
					
				}

				if (StompCommand.SEND.equals(accessor.getCommand())) {
					System.out.println("The message is: " + message.getPayload());
					System.out.println("Sending message to: " + accessor.getDestination());
					handleIntentionalDisconnection(accessor, message);
				}

				return message;
			}
		};
	}

	private void handleStompDisconnect(StompHeaderAccessor accessor) {
		// Abrupt disconnect
		CustomPrincipal principal = (CustomPrincipal) accessor.getUser();
		if (principal == null)
			return;

		System.out.println("Disconnecting user: " + principal.getName());
		// Todo: Implement logic to remove player from game. And to save game state.
		// And to notify the other player. XD
	};

	private void handleIntentionalDisconnection(StompHeaderAccessor accessor, Message<?> msg) {
		CustomPrincipal principal = (CustomPrincipal) accessor.getUser();
		if (principal == null)
			return;

		String payload = new String((byte[]) msg.getPayload());

		try {
			ObjectMapper mapper = new ObjectMapper();
			PlayerConnectionMessage disconnectMessage = mapper.readValue(payload, PlayerConnectionMessage.class);
			
			Player user = principal.getPlayer();
			
			switch (disconnectMessage.getType()) {
			case "REFRESH_INTENT":
				System.out.println("User " + principal.getName() + " refreshed the page with intent. The message is: "
						+ disconnectMessage.getMessage());
				reconnectingPlayers.put(user, System.currentTimeMillis());
				
				scheduleDisconnectionCheck(user);
				break;
			case "CONNECTED":
				System.out.println("User " + principal.getName() + " connected to the game. The message is: "
						+ disconnectMessage.getMessage());
				if(reconnectingPlayers.containsKey(user)) {
					System.out.println("User " + principal.getName() + " reconnected to the game.");
					reconnectingPlayers.remove(user);
				}
				break;
			
			case "NAVIGATION":
				System.out.println("User " + principal.getName() + " navigated away from the page."
						+ disconnectMessage.getPath() + ". The message is: " + disconnectMessage.getMessage());
				break;
			case "REFRESH":
				System.out.println("User " + principal.getName() + " refreshed the page. The message is: "
						+ disconnectMessage.getMessage());
				break;
			case "LEAVE":
				System.out.println("User " + principal.getName() + " left the game. The message is: "
						+ disconnectMessage.getMessage());
				break;
			default:
				System.out.println("Unknown message type: " + disconnectMessage.getType());

			}
		} catch (Exception e) {
			System.err.println("Error parsing disconnect message: " + e.getMessage());
		}
		;
	};
	
	private void scheduleDisconnectionCheck(Player user) {
	    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	    executor.schedule(()->{
	    	if(reconnectingPlayers.containsKey(user)) {
	    		reconnectingPlayers.remove(user);
	    		handlePlayerTimeout(user);
	    	}
	    	executor.shutdown();
	    }, 15, TimeUnit.SECONDS);
	}
	
	private void handlePlayerTimeout(Player user) {
		System.out.println("User " + user.getUserName() + " timed out.");
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

	private boolean isValidBattleShipSubscription(String urlDestination, CustomPrincipal principal) {
		if (principal == null) {
			System.out.println("Principal is null");
			return false;
		}

		if (urlDestination.startsWith("/topic/game")) {
			String gameId = urlDestination.substring("/topic/game".length());
			System.out.println("GameId: " + gameId);
			// TODO: Implement logic to check if player is already in the game.
			return true;
		}
		;

		return false;
	};
}
