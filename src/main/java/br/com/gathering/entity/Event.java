package br.com.gathering.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "event", schema = "gathering")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gathering.sequence_event")
	@SequenceGenerator(name = "gathering.sequence_event", sequenceName = "gathering.sequence_event", allocationSize = 1)
	private Long id;

	@Column(name = "id_gathering", nullable = false)
	private Long idGathering;

	@ManyToOne
	@JoinColumn(name = "id_gathering", nullable = true, insertable = false, updatable = false)
	private Gathering gathering;

	@Column(name = "id_format", nullable = false)
	private Long idFormat;

	@ManyToOne
	@JoinColumn(name = "id_format", nullable = true, insertable = false, updatable = false)
	private Format format;

	@Column(name = "canceled", nullable = false)
	private Boolean canceled;

	@Column(name = "finalized", nullable = false)
	private Boolean finalized;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "resultsAt")
	private LocalDateTime resultsAt;

	@Column(nullable = false)
    private Integer players;

    @Column(nullable = false)
    private Integer rounds;

	@Column(name = "confra_fee", nullable = false)
    private Double confraFee;

	@Column(name = "round_fee", nullable = false)
    private Double roundFee;

	@Column(name = "loser_pot", nullable = false)
    private Double loserPot;

	@Column(name = "confra_pot", nullable = false)
    private Double confraPot;

	@Column(name = "prize", nullable = false)
    private Double prize;

	@OrderBy("players ASC")
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true) 
	private List<EventFee> fees;

	public void init() {
		LocalDateTime now = LocalDateTime.now();

		this.canceled = (this.canceled == null) ? false : this.canceled;
		this.finalized = (this.finalized == null) ? false : this.finalized;
		this.createdAt = (this.createdAt == null) ? now : this.createdAt;
		this.updatedAt = (this.updatedAt == null) ? now : this.updatedAt;
	    this.players = (this.players == null) ? 0 : this.players;
	    this.rounds = (this.rounds == null) ? 0 : this.rounds;
	    this.confraFee = (this.confraFee == null) ? 0.0 : this.confraFee;
	    this.roundFee = (this.roundFee == null) ? 0.0 : this.roundFee;  
	    this.loserPot = (this.loserPot == null) ? 0.0 : this.loserPot;
	    this.confraPot = (this.confraPot == null) ? 0.0 : this.confraPot;
	    this.prize = (this.prize == null) ? 0.0 : this.prize;

	    if (fees != null) {
	        fees.forEach(fee -> {
	            fee.setIdEvent(this.id);
	            fee.setEvent(this);
	        });
	    }
	}

//	@Override
//	public String toString() {
//	    return "Event: {\n"
//	            + "\tid: " + this.id + ",\n"
//	            + "\tidGathering: " + this.idGathering + ",\n"
//	            + "\tidFormat: " + this.idFormat + ",\n"
//	            + "\tcreatedAt: " + this.createdAt + ",\n"
//	            + "\tplayers: " + this.players + ",\n"
//	            + "\trounds: " + this.rounds + ",\n"
//	            + "\tconfraFee: " + this.confraFee + ",\n"
//	            + "\troundFee: " + this.roundFee + ",\n"
//	            + "\tloserPot: " + this.loserPot + ",\n"
//	            + "\tconfraPot: " + this.confraPot + ",\n"
//	            + "\tprize: " + this.prize + ",\n"
//	            + "}";
//	}

	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Event: {\n");
        sb.append("\tidGathering: ").append(idGathering).append(",\n");
        sb.append("\tidFormat: ").append(idFormat).append(",\n");
        sb.append("\tcreatedAt: ").append(createdAt).append(",\n");
        sb.append("\tupdatedAt: ").append(updatedAt).append(",\n");
        sb.append("\tresultsAt: ").append(resultsAt).append(",\n");
        sb.append("\tplayers: ").append(players).append(",\n");
        sb.append("\trounds: ").append(rounds).append(",\n");
        sb.append("\tconfraFee: ").append(confraFee).append(",\n");
        sb.append("\troundFee: ").append(roundFee).append(",\n");
        sb.append("\tloserPot: ").append(loserPot).append(",\n");
        sb.append("\tconfraPot: ").append(confraPot).append(",\n");
        sb.append("\tprize: ").append(prize).append(",\n");

        // Exibir lista de fees
        sb.append("\tfees: ");
        if (fees == null || fees.isEmpty()) {
            sb.append("[]\n");
        } else {
            sb.append("[\n");
            for (EventFee fee : fees) {
                sb.append("\t\t{ players: ").append(fee.getPlayers())
                  .append(", prizeFee: ").append(fee.getPrizeFee())
                  .append(", loserFee: ").append(fee.getLoserFee())
                  .append(" },\n");
            }
            // Remove a vírgula final e quebra a linha corretamente
            if (!fees.isEmpty()) sb.setLength(sb.length() - 2);
            sb.append("\n\t]\n");
        }

        sb.append("}");
        return sb.toString();
    }

}
