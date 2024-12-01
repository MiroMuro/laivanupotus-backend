package com.miro.Laivanupotus.model;

import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Embeddable
@AllArgsConstructor
public class Coordinate {
	int x;
	int y;
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o
				.getClass()) {
			return false;
		}
		Coordinate that = (Coordinate) o;
		return x == that.x && y == that.y;
	}

	@Override
	public int hashCode() {
		return Objects
				.hash(x, y);
	}
}
