package com.miro.Laivanupotus.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id", referencedColumnName = "id")
    private List<Ship> ships;

    @ElementCollection
    @CollectionTable(name = "board_moves")
    private List<Move> moves;

    // Stores the board state as a 10x10 grid.
    // See data types of Ships for more context.
    @Column(length = 100)
    private String boardState;

    @ElementCollection
    private List<Coordinate> allShipsCoords;

    public boolean isValidPlacement(Ship ship) {
	System.out.println("Ship type: " + ship.getType().toString());
	List<Coordinate> shipCoords = ship.getCoordinates();
	System.out.println("COORDS:" + shipCoords);
	boolean isOverlappingCoordinates = shipsCoordinatesOverlap(shipCoords);

	if (!isOverlappingCoordinates) {
	    allShipsCoords.addAll(shipCoords);
	    return true;
	}

	return false;
    }



    public boolean isShipSunk(Ship ship) {
    System.out.println("In board class THe ship in question: " + ship);
	int hits = 0;
	int length = ship.getType().getLength();
	//System.out.println("Ship length: " + length);
	List<Coordinate> shipCoords = ship.getCoordinates();
	//System.out.println("Ship coords: " + shipCoords);
	//System.out.println("Moves: " + this.moves);
	for (int i = 0; i < shipCoords.size(); i++) {
	    int x = shipCoords.get(i).getX();
	    int y = shipCoords.get(i).getY();

	    for (Move move : this.moves) {
		if (move.getX() == x && move.getY() == y) {
		    hits++;
		    System.out.println("HIT!");
		    break;
		}
		;
	    }
	}
	System.out.println("in board class pHits: " + hits);
	System.out.println("in board class Length: " + length);
	Boolean sunk = hits == length;
	System.out.println("in board class Sunk: " + sunk);
	;
	return hits == length;
    }



    public boolean shipsCoordinatesOverlap(List<Coordinate> currentShipCoords) {

	for (Coordinate shipCoords : currentShipCoords) {
	    if (allShipsCoords.contains(shipCoords)) {
		return true;
	    }
	}

	return false;
    };
}
