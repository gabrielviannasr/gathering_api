package br.com.gathering.dto.response;

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
public class EventFeeResponseDTO {

    private Long idEvent;
    private Integer players;
    private Double prizeFee;
    private Double loserFee;

}