package com.miro.Laivanupotus.service;

import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.model.Player;

@Service
public interface ConnectionEventService {
	public void publisDisconnection(Player player);
	
	public void publishTimeOut(Player player);
	
	public void publishConnection(Player player);
	
	public void publishReconnection(Player player);
}
