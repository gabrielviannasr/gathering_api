package br.com.gathering.dto.request;

import java.time.LocalDateTime;

import br.com.gathering.entity.Transaction;
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
public class TransactionDTO {

	public static final int DESCRIPTION_LENGTH = 20;

	// Not needed in post method
	// private Long id;

	private Long idGathering;

	private Long idEvent;

	private Long idPlayer;

	private Long idTransactionType;

	private LocalDateTime createdAt;

	private Double amount;

    private String description;

    public Transaction toModel() {
    	Transaction transaction = new Transaction();
    	transaction.setIdGathering(this.idGathering);
    	transaction.setIdEvent(this.idEvent);
    	transaction.setIdPlayer(this.idPlayer);
    	transaction.setIdTransactionType(this.idTransactionType);
    	transaction.setCreatedAt(this.createdAt);
    	transaction.setAmount(this.amount);
    	transaction.setDescription(this.description);

    	return transaction;
    }

	@Override
    public String toString() {
		return "TransactionDTO: {\n"
//				+ "\tid: " + this.id + ",\n"
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
