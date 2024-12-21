package com.miro.Laivanupotus.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class ErrorResponseDto {
	private String message;
	private String error;
	private int status;
	private LocalDateTime timestamp;
}
