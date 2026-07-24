package br.com.gathering.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
@Table(name = "transaction", schema = "gathering")
public class Transaction {

	public static final int DESCRIPTION_LENGTH = 500;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gathering.sequence_transaction")
	@SequenceGenerator(name = "gathering.sequence_transaction", sequenceName = "gathering.sequence_transaction", allocationSize = 1)
	private Long id;

	@Column(name = "id_gathering", nullable = false)
	private Long idGathering;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "id_gathering", nullable = true, insertable = false, updatable = false)
	private Gathering gathering;

	@Column(name = "id_event", nullable = true)
	private Long idEvent;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "id_event", nullable = true, insertable = false, updatable = false)
	private Event event;

	@Column(name = "id_player", nullable = false)
	private Long idPlayer;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "id_player", nullable = true, insertable = false, updatable = false)
	private Player player;

	@Column(name = "id_transaction_type", nullable = false)
	private Long idTransactionType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ManyToOne
	@JoinColumn(name = "id_transaction_type", nullable = true, insertable = false, updatable = false)
	private TransactionType type;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "amount", nullable = false)
	private Double amount;

	@Column(length = DESCRIPTION_LENGTH)
	private String description;

	@Transient
	public String getGatheringName() {
		return this.gathering.getName();
	}

	@Transient
	public String getPlayerName() {
		return this.player.getName();
	}

	public void init() {
		this.createdAt = (this.createdAt == null) ? LocalDateTime.now() : this.createdAt;
		this.amount = (this.amount == null) ? 0 : this.amount;
	}

	@Override
	public String toString() {
		return "Transaction: {\n"
				+ "\tid: " + this.id + ",\n"
				+ "\tidGathering: " + this.idGathering + ",\n"
				+ "\tidEvent: " + this.idEvent + ",\n"
				+ "\tidPlayer: " + this.idPlayer + ",\n"
				+ "\tidTransactionType: " + this.idTransactionType + ",\n"
				+ "\tcreatedAt: " + this.createdAt + ",\n"
				+ "\tamount: " + this.amount + ",\n"
				+ "\tdescription: " + this.description + ",\n"
				+ "}";
	}

}
