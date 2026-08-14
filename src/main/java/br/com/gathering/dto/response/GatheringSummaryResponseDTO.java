package br.com.gathering.dto.response;

import br.com.gathering.projection.gathering.GatheringSummaryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GatheringSummaryResponseDTO {

	private Long id;
	private Integer year;
	private String name;
	
	private Integer events;
	private Integer players;
	private Integer rounds;
	
    private Double loserPot;
    private Double confraPot;
    private Double prize;
    
    public static GatheringSummaryResponseDTO from(GatheringSummaryProjection model) {
    	return GatheringSummaryResponseDTO.builder()
    			.id(model.getId())
    			.year(model.getYear())
    			.name(model.getName())
    			.events(model.getEvents())
    			.players(model.getPlayers())
    			.rounds(model.getRounds())
    			.loserPot(model.getLoserPot())
    			.confraPot(model.getConfraPot())
    			.prize(model.getPrize())
    			.build();
    }

}
