package com.miro.Laivanupotus.model;

import java.time.LocalDateTime;

import com.miro.Laivanupotus.interfaces.UserProfileDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "players")
@Data
@Getter
@Setter
public class User implements UserProfileDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String userName;
	@Column(nullable = false)
	private String email;
	@Column(unique = true, nullable = false)
	private String password;

	private int totalGames;
	private int gamesWon;
	private int gamesLost;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime lastLogin;
	private String roles;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}
}
