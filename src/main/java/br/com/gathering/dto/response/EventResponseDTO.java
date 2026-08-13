package br.com.gathering.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventResponseDTO {

    private Long id;
    private Long idGathering;
    private GatheringResponseDTO gathering;

    private Long idFormat;
    private FormatResponseDTO format;

    private Boolean canceled;
    private Boolean finalized;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resultsAt;

    private Integer players;
    private Integer rounds;

    private Double confraFee;
    private Double roundFee;
    private Double loserPot;
    private Double confraPot;
    private Double prize;

    private List<EventFeeResponseDTO> fees;

}
