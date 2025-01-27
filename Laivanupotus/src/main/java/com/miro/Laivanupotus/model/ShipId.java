package com.miro.Laivanupotus.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipId implements Serializable {
    private Long id;
    private Long boardId;

    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (o == null || getClass() != o.getClass()) {
	    return false;
	}
	ShipId shipId = (ShipId) o;
	return id.equals(shipId.id) && boardId.equals(shipId.boardId);
    }

    @Override
    public int hashCode() {
	return java.util.Objects.hash(id, boardId);
    }
};
