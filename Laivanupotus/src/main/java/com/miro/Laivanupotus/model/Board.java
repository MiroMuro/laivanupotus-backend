package com.miro.Laivanupotus.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Ship> ships;

	@ElementCollection
	private List<Move> moves;

	// Stores the board state as a 10x10 grid.
	// See data types of Ships for more context.
	@Column(length = 100)
	private String boardState;

	public boolean isValidPlacement(Ship ship) {
		// Todo: implement
		return true;
	}

	public boolean makeAMove(Move move) {
		// Implementation for making a move
		return false;
	}

	public boolean isShipSunk(Ship ship) {
		int hits = 0;
		int length = ship.getType().getLength();
		boolean isVertical = ship.isVertical();
		for (int i = 0; i < length; i++) {
			int x = ship.getX() + (isVertical ? 0 : i);
			int y = ship.getY() + (isVertical ? i : 0);

			for (Move move : this.moves) {
				if (move.getX() == x && move.getY() == y) {
					hits++;
					break;
				}
				;
			}
		}
		;
		return hits == length;
	}
}
