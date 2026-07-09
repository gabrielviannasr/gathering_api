package br.com.gathering.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "round", schema = "gathering")
public class Round {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gathering.sequence_round")
	@SequenceGenerator(name = "gathering.sequence_round", sequenceName = "gathering.sequence_round", allocationSize = 1)
	private Long id;

	@Column(name = "id_event", nullable = false)
	private Long idEvent;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "id_event", nullable = true, insertable = false, updatable = false)
	private Event event;

	@Column(name = "id_format", nullable = false)
	private Long idFormat;

	@ManyToOne
	@JoinColumn(name = "id_format", nullable = true, insertable = false, updatable = false)
	private Format format;

	@Column(name = "id_player_winner", nullable = true)
	private Long idPlayerWinner;

	@ManyToOne
	@JoinColumn(name = "id_player_winner", nullable = true, insertable = false, updatable = false)
	private Player playerWinner;	

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

   @Column(nullable = false)
    private Integer round;

	@Column(name = "players", nullable = false)
    private Integer playersTotal;

	@Column(name = "prize", nullable = false)
    private Double prize;

	@Column(name = "loser_pot", nullable = false)
    private Double loserPot;

	@Column(nullable = false)
    private Boolean canceled;

	@ManyToMany
	@JoinTable(
	    name = "round_player",
	    schema = "gathering",
	    joinColumns = @JoinColumn(name = "id_round"),
	    inverseJoinColumns = @JoinColumn(name = "id_player")
	)
	private List<Player> players;

	public void init() {
	    this.createdAt = (this.createdAt == null) ? LocalDateTime.now() : this.createdAt;
	    this.playersTotal = (this.playersTotal == null) ? 0 : this.playersTotal;
	    this.prize = (this.prize == null) ? 0 : this.prize;
	    this.loserPot = (this.loserPot == null) ? 0 : this.loserPot;
	    this.canceled = (this.canceled == null) ? false : this.canceled;
	}

	@Override
	public String toString() {
	    return "Round: {\n"
	            + "\tid: " + this.id + ",\n"
	            + "\tidEvent: " + this.idEvent + ",\n"
	            + "\tidFormat: " + this.idFormat + ",\n"
	            + "\tidPlayerWinner: " + this.idPlayerWinner + ",\n"
	            + "\tcreatedAt: " + this.createdAt + ",\n"
	            + "\tround: " + this.round + ",\n"
	            + "\tplayersTotal: " + this.playersTotal + ",\n"
	            + "\tprize: " + this.prize + ",\n"
	            + "\tloserPot: " + this.loserPot + ",\n"
	            + "\tcanceled: " + this.canceled + ",\n"
	            + "}";
	}

}
