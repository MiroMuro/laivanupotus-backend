package com.miro.Laivanupotus.event;

import com.miro.Laivanupotus.model.Player;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerConnectionEvent {
	private final String playerUserName;
	private final Long playerId;
	private final ConnectionStatus status;
	private final String message;
	
	public enum ConnectionStatus {
		CONNECTED,RECONNECTED, DISCONNECTED, TIMED_OUT
	}
}
