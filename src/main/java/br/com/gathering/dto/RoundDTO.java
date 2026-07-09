package br.com.gathering.dto;

import java.time.LocalDateTime;

import br.com.gathering.entity.Round;
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
public class RoundDTO {

	// Not needed in post method
	// private Long id;

	private Long idEvent;

	private Long idFormat;

	private Long idPlayerWinner;	

	private LocalDateTime createdAt;

    private Integer round;

    private Integer playersTotal;

    private Double prize;

    private Double loserPot;

    private Boolean canceled;

    public Round toModel() {
        Round round = new Round();
//        round.setId(this.id);
        round.setIdEvent(this.idEvent);
        round.setIdFormat(this.idFormat);
        round.setIdPlayerWinner(this.idPlayerWinner);
        round.setCreatedAt(this.createdAt);
        round.setRound(this.round);
        round.setPlayersTotal(this.playersTotal);
        round.setPrize(this.prize);
        round.setLoserPot(this.loserPot);
        round.setCanceled(this.canceled);

        return round;
    }

	@Override
	public String toString() {
	    return "RoundDTO: {\n"
//	            + "\tid: " + this.id + ",\n"
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
