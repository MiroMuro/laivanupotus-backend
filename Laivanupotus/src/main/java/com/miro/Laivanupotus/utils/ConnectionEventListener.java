package com.miro.Laivanupotus.utils;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.miro.Laivanupotus.event.PlayerConnectionEvent;
import com.miro.Laivanupotus.model.PlayerConnectionMessage;

@Component
public class ConnectionEventListener {
	private final SimpMessagingTemplate messagingTemplate;
	
	public ConnectionEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
	
	@EventListener
	public void handlePlayerConnectionEvent(PlayerConnectionEvent event) {
		Long matchId = event.getMatchId();
		// Implement a better message class.
		//PlayerConnectionMessage message = new PlayerConnectionMessage(event.getPlayer().getId(), event.getStatus());
		
		messagingTemplate.convertAndSend("/topic/game/"+matchId+"/connections", event);
	};
}
