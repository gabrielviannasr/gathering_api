package br.com.gathering.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "score", schema = "gathering")
public class Score {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gathering.sequence_score")
	@SequenceGenerator(name = "gathering.sequence_score", sequenceName = "gathering.sequence_score", allocationSize = 1)
	private Long id;

	@Column(name = "id_round", nullable = false)
	private Long idRound;

	@ManyToOne
	@JoinColumn(name = "id_round", nullable = true, insertable = false, updatable = false)
	private Round round;

	@Column(name = "id_player", nullable = false)
	private Long idPlayer;

	@ManyToOne
	@JoinColumn(name = "id_player", nullable = true, insertable = false, updatable = false)
	private Player player;

//	@Column(name = "id_player_killed_by", nullable = true)
//	private Long idPlayerKilledBy;
//
//	@ManyToOne
//	@JoinColumn(name = "id_player_killed_by", nullable = true, insertable = false, updatable = false)
//	private Player playerKilledBy;
//
//	@Column(name="is_dead", nullable = false)
//    private Boolean isDead;
//
//	@Column(name = "primary_commander_name")
//    private String primaryCommanderName;
//    
//    @Column(name = "primary_commander_count", nullable = false)
//    private Integer primaryCommanderCount;
//
//    @Column(name = "secondary_commander_name")
//    private String secondaryCommanderName;
//
//    @Column(name = "secondary_commander_count")
//    private Integer secondaryCommanderCount;
//
//	@Column(name = "infect_count", nullable = false)
//    private Integer infectCount;
//
//	@Column(name = "life_count", nullable = false)
//    private Integer lifeCount;

	public void init() {
//		this.isDead = (this.isDead == null) ? false : this.isDead;
//	    this.primaryCommanderCount = (this.primaryCommanderCount == null) ? 0 : this.primaryCommanderCount;
//	    this.secondaryCommanderCount = (this.secondaryCommanderCount == null) ? 0 : this.secondaryCommanderCount;
//	    this.infectCount = (this.infectCount == null) ? 0 : this.infectCount;
	}

	@Override
	public String toString() {
	    return "RoundPlayer: {\n"
	            + "\tid: " + this.id + ",\n"
	            + "\tidRound: " + this.idRound + ",\n"
	            + "\tidPlayer: " + this.idPlayer + ",\n"
//	            + "\tidPlayerKilledBy: " + this.idPlayerKilledBy + ",\n"
//	            + "\tisDead: " + this.isDead + ",\n"
//	            + "\tprimaryCommanderName: " + this.primaryCommanderName + ",\n"
//	            + "\tprimaryCommanderCount: " + this.primaryCommanderCount + ",\n"
//	            + "\tsecondaryCommanderName: " + this.secondaryCommanderName + ",\n"
//	            + "\tsecondaryCommanderCount: " + this.secondaryCommanderCount + ",\n"
//	            + "\tinfectCount: " + this.infectCount + ",\n"
//	            + "\tlifeCount: " + this.lifeCount + ",\n"
	            + "}";
	}

}
