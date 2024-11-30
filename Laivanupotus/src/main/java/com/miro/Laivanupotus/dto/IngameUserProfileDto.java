package com.miro.Laivanupotus.dto;

import com.miro.Laivanupotus.interfaces.UserProfileDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngameUserProfileDto implements UserProfileDto {
	private Long id;
	private String userName;
}

