package model;

import java.time.LocalDateTime;

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
	private User player1;

	@ManyToOne
	private User player2;

	@OneToOne(cascade = CascadeType.ALL)
	private Board player1Board;

	@OneToOne(cascade = CascadeType.ALL)
	private Board player2Board;

	@Enumerated(EnumType.STRING)
	private GameStatus status;

	@Column(name = "current_turn")
	private Long currentTurnPlayerId;

	private LocalDateTime startTime;
	private LocalDateTime updatedAt;
	private LocalDateTime endedAt;

	public enum GameStatus {
		WAITING_FOR_PLAYER, PLACING_SHIPS, IN_PROGRESS, FINISHED
	}

}
