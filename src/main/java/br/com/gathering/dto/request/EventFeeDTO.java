package br.com.gathering.dto.request;

import br.com.gathering.entity.EventFee;
import br.com.gathering.entity.EventFeeId;
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

        EventFeeId id = new EventFeeId();
        id.setPlayers(players);

        fee.setId(id);
        fee.setPrizeFee(prizeFee);
        fee.setLoserFee(loserFee);

        return fee;
    }
}