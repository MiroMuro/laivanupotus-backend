package com.miro.Laivanupotus.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerConnectionMessage {
	private String type;
	private String path;
	private long timestamp;
	private String message;
	private long playerId;

}
