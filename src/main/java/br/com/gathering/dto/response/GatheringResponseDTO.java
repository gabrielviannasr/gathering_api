package br.com.gathering.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GatheringResponseDTO {

	private Long id;
    private String name;
	private Integer year;

}
