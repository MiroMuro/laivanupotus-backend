package com.miro.Laivanupotus.model;

import java.time.LocalDateTime;

import com.miro.Laivanupotus.Enums.GameStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Match {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Player player1;

	@ManyToOne
	private Player player2;

	@OneToOne(cascade = CascadeType.ALL)
	private Board player1Board;

	@OneToOne(cascade = CascadeType.ALL)
	private Board player2Board;

	@Enumerated(EnumType.STRING)
	private GameStatus status;

	@Column(name = "current_turn")
	private Long currentTurnPlayerId;
	
	@Column(name = "winner_id", nullable = true)
	private Long winnerId;
	
	private LocalDateTime startTime;
	private LocalDateTime updatedAt;
	private LocalDateTime endedAt;



}
