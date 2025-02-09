package com.miro.Laivanupotus.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.miro.Laivanupotus.dto.WebSocketActiveMatchResponseDto;

@Controller
public class GameWebSocketHandler {

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameWebSocketHandler(SimpMessagingTemplate messagingTemplate) {
	this.messagingTemplate = messagingTemplate;
    }


    public void notifyGameUpdate(Long matchId, Object gameState) {
	// Send game updates to all subscribers of this match
	messagingTemplate.convertAndSend("/topic/game/" + matchId, gameState);
    };

    public void notifyShipsPlaced(Long matchId, WebSocketActiveMatchResponseDto message) {
	messagingTemplate.convertAndSend("/topic/game/" + matchId + "/ships-placed");
    }

    public void notifyPlayerJoined(Long matchId, WebSocketActiveMatchResponseDto message) {
	messagingTemplate.convertAndSend(
		"/topic/game/" + matchId + "/player-joined", message);
    };

    public void notifyMoveMade(Long matchId, Object moveResult) {
	System.out.println("The move result is: " + moveResult);
	messagingTemplate.convertAndSend("/topic/game/" + matchId + "/move",
		moveResult);
    };
    
    public void notifyOpponentDisconnect(Long matchId) {
    	System.out.println("Opponent disconnected");
    	messagingTemplate.convertAndSend(
    			"/topic/game/" + matchId + "/opponent-disconnected","Opponent disconnected");
    	    };
    
}
