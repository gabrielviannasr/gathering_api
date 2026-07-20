package br.com.gathering.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GatheringFormatResponseDTO {

	private Long idFormat;
	private FormatResponseDTO format;
	private Integer rounds;

}
