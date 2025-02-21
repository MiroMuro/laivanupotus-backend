package com.miro.Laivanupotus.serviceImp;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.Enums.GameStatus;
import com.miro.Laivanupotus.event.PlayerConnectionEvent;
import com.miro.Laivanupotus.exceptions.MatchNotFoundException;
import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.repository.MatchRepository;
import com.miro.Laivanupotus.service.ConnectionEventService;

@Service
public class ConnectionEventServiceImpl implements ConnectionEventService {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private MatchRepository matchRepository;

	@Override
	public void publisDisconnection(Player player, String message) throws MatchNotFoundException {
		try {
			PlayerConnectionEvent event = constructEvent(player, PlayerConnectionEvent.ConnectionStatus.DISCONNECTED,
					message);

			publisher.publishEvent(event);
		} catch (MatchNotFoundException e) {
			System.out.println("Disconnection event could not be published: {}" + e.getMessage());
		}
		

	}

	@Override
	public void publishTimeOut(Player player, String message) {
		PlayerConnectionEvent event = constructEvent(player, PlayerConnectionEvent.ConnectionStatus.TIMED_OUT, message);
		publisher.publishEvent(event);

	}

	@Override
	public void publishConnection(Player player, String message) {
		PlayerConnectionEvent event = constructEvent(player, PlayerConnectionEvent.ConnectionStatus.CONNECTED, message);
		publisher.publishEvent(event);

	}

	@Override
	public void publishReconnection(Player player, String message) {
		PlayerConnectionEvent event = constructEvent(player, PlayerConnectionEvent.ConnectionStatus.RECONNECTED,
				message);
		publisher.publishEvent(event);

	}

	private PlayerConnectionEvent constructEvent(Player player,
			PlayerConnectionEvent.ConnectionStatus playerConnectionStatus, String message) {

		// As the matchId cannot be passed through WebSocket headers (Browsers block
		// custom headers often), we have to get it with a query
		// using the players id and the game status.
		try {
			Long matchId = matchRepository.getOngoingMatchId(player.getId(), GameStatus.FINISHED).orElseThrow(
					() -> new MatchNotFoundException("No active match found for player: " + player.getUsername()));
			return new PlayerConnectionEvent(player.getUsername(), player.getId(), playerConnectionStatus, message,
					matchId);
		} catch (MatchNotFoundException e) {
			System.out.println("Connection event could not be published: {}"+e.getMessage());
			return new PlayerConnectionEvent(player.getUsername(), player.getId(), playerConnectionStatus, 
                    "No active match found", -1L);
		}

	}

}
