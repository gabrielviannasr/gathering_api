package br.com.gathering.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import br.com.gathering.entity.Event;
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
public class EventDTO {

	// Not needed in post method
	// private Long id;

	private Long idGathering;

	private Long idFormat;

	private LocalDateTime createdAt;
//
//    private Integer players;
//
//    private Integer rounds;

    private Double confraFee;

    private Double roundFee;

//    private Double confraPot;
//
//    private Double loserPot;
//
//    private Double prize;
    
    private List<EventFeeDTO> fees;

    public Event toModel() {
        Event event = new Event();
//        event.setId(this.id);
        event.setIdGathering(this.idGathering);
        event.setIdFormat(this.idFormat);
        event.setCreatedAt(this.createdAt);
//        event.setPlayers(this.players);
//        event.setRounds(this.rounds);
        event.setConfraFee(this.confraFee);
        event.setRoundFee(this.roundFee);
//        event.setLoserPot(this.loserPot);
//        event.setConfraPot(this.confraPot);
//        event.setPrize(this.prize);
        event.setFees(
    	    fees == null
    	        ? List.of()
    	        : fees.stream()
    	              .map(EventFeeDTO::toModel)
    	              .toList()
    	);

        return event;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EventDTO: {\n");
        sb.append("\tidGathering: ").append(idGathering).append(",\n");
        sb.append("\tidFormat: ").append(idFormat).append(",\n");
//        sb.append("\tcreatedAt: ").append(createdAt).append(",\n");
//        sb.append("\tplayers: ").append(players).append(",\n");
//        sb.append("\trounds: ").append(rounds).append(",\n");
        sb.append("\tconfraFee: ").append(confraFee).append(",\n");
        sb.append("\troundFee: ").append(roundFee).append(",\n");
//        sb.append("\tloserPot: ").append(loserPot).append(",\n");
//        sb.append("\tconfraPot: ").append(confraPot).append(",\n");
//        sb.append("\tprize: ").append(prize).append(",\n");

        // Exibir lista de fees
        sb.append("\tfees: ");
        if (fees == null || fees.isEmpty()) {
            sb.append("[]\n");
        } else {
            sb.append("[\n");
            for (EventFeeDTO fee : fees) {
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
