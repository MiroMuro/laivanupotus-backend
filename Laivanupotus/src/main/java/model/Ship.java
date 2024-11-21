package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Ship {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private ShipType type;
	private int x;
	private int y;
	private boolean isVertical;
	private boolean isSunk;

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
