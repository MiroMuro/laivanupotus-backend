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
	public void publisDisconnection(Player player) {
		publisher.publishEvent(new PlayerConnectionEvent(player, PlayerConnectionEvent.ConnectionStatus.DISCONNECTED));
		
	}

	@Override
	public void publishTimeOut(Player player) {
		publisher.publishEvent(new PlayerConnectionEvent(player, PlayerConnectionEvent.ConnectionStatus.TIMED_OUT));

		
	}

	@Override
	public void publishConnection(Player player) {
		publisher.publishEvent(new PlayerConnectionEvent(player, PlayerConnectionEvent.ConnectionStatus.CONNECTED));

		
	}

	@Override
	public void publishReconnection(Player player) {
		publisher.publishEvent(new PlayerConnectionEvent(player, PlayerConnectionEvent.ConnectionStatus.RECONNECTED));

	}

}
