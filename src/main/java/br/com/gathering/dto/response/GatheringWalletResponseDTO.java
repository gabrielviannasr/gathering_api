package br.com.gathering.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GatheringWalletResponseDTO {

	private Long idPlayer;
	private PlayerResponseDTO player;
	private Double wallet;

}
