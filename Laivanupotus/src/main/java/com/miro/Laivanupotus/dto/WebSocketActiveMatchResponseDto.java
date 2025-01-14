package com.miro.Laivanupotus.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class WebSocketActiveMatchResponseDto extends ActiveMatchResponseDto {
    private String message;
    private boolean messageStatus;
}
