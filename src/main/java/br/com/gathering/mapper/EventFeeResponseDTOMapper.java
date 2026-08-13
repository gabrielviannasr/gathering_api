package br.com.gathering.mapper;

import br.com.gathering.dto.response.EventFeeResponseDTO;
import br.com.gathering.entity.EventFee;

public class EventFeeResponseDTOMapper {

    public static EventFeeResponseDTO fromModel(EventFee model) {
        return EventFeeResponseDTO.builder()
                .idEvent(model.getId().getIdEvent())
                .players(model.getId().getPlayers())
                .prizeFee(model.getPrizeFee())
                .loserFee(model.getLoserFee())
                .build();
    }

}
