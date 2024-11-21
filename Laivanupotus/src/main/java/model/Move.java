package model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Move {
	private int x;
	private int y;
	private Long playerBehindTheMoveId;
	private boolean isHit;
}
