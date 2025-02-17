package com.miro.Laivanupotus.event;

import com.miro.Laivanupotus.model.Player;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerConnectionEvent {
	private final Player player;
	private final ConnectionStatus status;
	
	public enum ConnectionStatus {
		CONNECTED,RECONNECTED, DISCONNECTED, TIMED_OUT
	}
}
