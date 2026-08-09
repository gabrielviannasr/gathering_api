package br.com.gathering.mapper;

import br.com.gathering.dto.response.GatheringResultResponseDTO;
import br.com.gathering.dto.response.PlayerResponseDTO;
import br.com.gathering.projection.gathering.ResultProjection;

public final class GatheringResultResponseMapper {

	public static GatheringResultResponseDTO from(ResultProjection model) {
		return GatheringResultResponseDTO.builder()
	            .idPlayer(model.getIdPlayer())
                .player(
                        PlayerResponseDTO.builder()
                        	.id(model.getIdPlayer())
                            .name(model.getPlayerName())
                            .build())
	            .rank(model.getRank())
	            .events(model.getEvents())
	            .wins(model.getWins())
	            .rounds(model.getRounds())
	            .positive(model.getPositive())
	            .negative(model.getNegative())
	            .rankBalance(model.getRankBalance())
	            .loserPot(model.getLoserPot())
	            .confraPot(model.getConfraPot())
	            .finalBalance(model.getFinalBalance())
	            .build();
	}

}
