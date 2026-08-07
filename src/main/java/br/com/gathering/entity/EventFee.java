package br.com.gathering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "event_fee", schema = "gathering")
public class EventFee {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gathering.sequence_event_fee")
	@SequenceGenerator(name = "gathering.sequence_event_fee", sequenceName = "gathering.sequence_event_fee", allocationSize = 1)
	private Long id;

	@Column(name = "id_event", nullable = false)
	private Long idEvent;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "id_event", nullable = true, insertable = false, updatable = false)
	private Event event;

	@Column(nullable = false)
    private Integer players;

	@Column(name = "prize_fee", nullable = false)
    private Double prizeFee;

	@Column(name = "loser_fee", nullable = false)
    private Double loserFee;

	public void init() {
	    this.players = (this.players == null) ? 0 : this.players;
	    this.prizeFee = (this.prizeFee == null) ? 0 : this.prizeFee;
	    this.loserFee = (this.loserFee == null) ? 0.0 : this.loserFee;
	}

	@Override
	public String toString() {
	    return "EventFee: {\n"
	            + "\tid: " + this.id + ",\n"
	            + "\tidEvent: " + this.idEvent + ",\n"
	            + "\tplayers: " + this.players + ",\n"
	            + "\tprizeFee: " + this.prizeFee + ",\n"
	            + "\tloserFee: " + this.loserFee + ",\n"
	            + "}";
	}

}
