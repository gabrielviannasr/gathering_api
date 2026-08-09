package br.com.gathering.mapper;

import br.com.gathering.dto.response.FormatResponseDTO;
import br.com.gathering.dto.response.GatheringFormatResponseDTO;
import br.com.gathering.projection.gathering.FormatProjection;

public final class GatheringFormatResponseMapper {

    public static GatheringFormatResponseDTO from(FormatProjection model) {
        return GatheringFormatResponseDTO.builder()
        		.idFormat(model.getIdFormat())
                .format(
                		FormatResponseDTO.builder()
                			.id(model.getIdFormat())
                            .name(model.getFormatName())
                            .build())
                .rounds(model.getRounds())
                .build();
    }

}
