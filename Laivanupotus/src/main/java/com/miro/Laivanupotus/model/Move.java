package com.miro.Laivanupotus.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Move {
    private int x;
    private int y;
    private Long playerBehindTheMoveId;
    @JsonProperty("isHit")
    private boolean isHit;
}
