package br.com.gathering.mapper;

import org.springframework.stereotype.Component;

import br.com.gathering.dto.response.EventFeeResponseDTO;
import br.com.gathering.dto.response.EventResponseDTO;
import br.com.gathering.dto.response.FormatResponseDTO;
import br.com.gathering.dto.response.GatheringResponseDTO;
import br.com.gathering.entity.Event;
import br.com.gathering.entity.EventFee;

@Component
public class EventMapper {

    public EventResponseDTO toResponse(Event event) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .idGathering(event.getIdGathering())
                .gathering(
                    GatheringResponseDTO.builder()
                        .id(event.getGathering().getId())
                        .year(event.getGathering().getYear())
                        .name(event.getGathering().getName())
                        .build()
                )
                .idFormat(event.getIdFormat())
                .format(
                    FormatResponseDTO.builder()
                        .id(event.getFormat().getId())
                        .name(event.getFormat().getName())
                        .lifeCount(event.getFormat().getLifeCount())
                        .build()
                )
                .canceled(event.getCanceled())
                .finalized(event.getFinalized())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .resultsAt(event.getResultsAt())
                .players(event.getPlayers())
                .rounds(event.getRounds())
                .confraFee(event.getConfraFee())
                .roundFee(event.getRoundFee())
                .loserPot(event.getLoserPot())
                .confraPot(event.getConfraPot())
                .prize(event.getPrize())
                .fees(
                    event.getFees().stream()
                        .map(this::toFeeResponse)
                        .toList()
                )
                .build();
    }

    private EventFeeResponseDTO toFeeResponse(EventFee fee) {
        return EventFeeResponseDTO.builder()
                .idEvent(fee.getId().getIdEvent())
                .players(fee.getId().getPlayers())
                .prizeFee(fee.getPrizeFee())
                .loserFee(fee.getLoserFee())
                .build();
    }
}
