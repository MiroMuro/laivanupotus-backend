package com.miro.Laivanupotus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestDto {
	private String userName;
	private String password;
}
