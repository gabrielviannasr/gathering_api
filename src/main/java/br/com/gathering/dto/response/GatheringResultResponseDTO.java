package br.com.gathering.dto.response;

import br.com.gathering.entity.Player;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GatheringResultResponseDTO {

	private Long idPlayer;
    private Player player;
    
    private Integer rank;
    private Integer events;
    private Integer wins;
    private Integer rounds;

    private Double positive;
    private Double negative;

    private Double rankBalance;
    private Double loserPot;
    private Double confraPot;
    private Double finalBalance;
}