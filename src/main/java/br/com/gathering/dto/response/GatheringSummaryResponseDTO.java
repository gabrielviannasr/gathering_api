package br.com.gathering.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GatheringSummaryResponseDTO {

	private Long idGathering;
	private GatheringResponseDTO gathering;
	
	private Integer events;
	private Integer players;
	private Integer rounds;
	
    private Double loserPot;
    private Double confraPot;
    private Double prize;
}
