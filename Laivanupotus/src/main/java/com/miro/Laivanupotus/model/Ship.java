package com.miro.Laivanupotus.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Ship {

    @Id
    private Long id;

    private ShipType type;
    @ElementCollection
    @CollectionTable(name = "ship_coordinates")
    private List<Coordinate> coordinates;
    private String direction;
    private boolean isSunk;
    private int height;
    private int width;

    public enum ShipType {
	CARRIER(5), BATTLESHIP(4), CRUISER(3), SUBMARINE(3), DESTROYER(2), WARBOAT(1);

	private final int length;

	ShipType(int length) {
	    this.length = length;
	    // TODO Auto-generated constructor stub
	}

	public int getLength() {
	    return length;
	}
    }

}
