package br.com.gathering.dto.request;

import br.com.gathering.entity.EventFee;
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
public class EventFeeDTO {

    private Integer players;
    private Double prizeFee;
    private Double loserFee;

    public EventFee toModel() {
        EventFee fee = new EventFee();
        fee.setPlayers(players);
        fee.setPrizeFee(prizeFee);
        fee.setLoserFee(loserFee);
        return fee;
    }
}