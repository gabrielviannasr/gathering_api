package br.com.gathering.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GatheringResponseDTO {


	private Long id;
	private Integer year;
    private String name;

}
