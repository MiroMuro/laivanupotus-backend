package com.miro.Laivanupotus.config;

import java.security.Principal;

import com.miro.Laivanupotus.model.Player;

public class CustomPrincipal implements Principal {

    private final Player player;

    public CustomPrincipal(Player player) {
	this.player = player;
    }

    @Override
    public String getName() {
	return player.getUserName(); // Return the unique identifier, like username
    }

    public Player getPlayer() {
	return player;
    }
}
