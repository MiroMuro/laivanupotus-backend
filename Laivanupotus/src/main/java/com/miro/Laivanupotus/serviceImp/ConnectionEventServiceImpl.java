package com.miro.Laivanupotus.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.event.PlayerConnectionEvent;
import com.miro.Laivanupotus.model.Player;
import com.miro.Laivanupotus.service.ConnectionEventService;

@Service
public class ConnectionEventServiceImpl implements ConnectionEventService {
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Override
	public void publisDisconnection(Player player, String message) {
		
		PlayerConnectionEvent event = constructEvent(player, PlayerConnectionEvent.ConnectionStatus.DISCONNECTED, message);
		
		publisher.publishEvent(event);
		
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
		PlayerConnectionEvent event = constructEvent(player, PlayerConnectionEvent.ConnectionStatus.RECONNECTED, message);
		publisher.publishEvent(event);
		

	}
	
	private PlayerConnectionEvent constructEvent(Player player, PlayerConnectionEvent.ConnectionStatus status, String message) {
		return new PlayerConnectionEvent(player.getUsername(), player.getId(), status, message);
	}

}
