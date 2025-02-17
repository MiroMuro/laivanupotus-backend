package com.miro.Laivanupotus.service;

import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.model.Player;

@Service
public interface ConnectionEventService {
	public void publisDisconnection(Player player, String message);
	
	public void publishTimeOut(Player player, String message);
	
	public void publishConnection(Player player, String message);
	
	public void publishReconnection(Player player, String message);
}
