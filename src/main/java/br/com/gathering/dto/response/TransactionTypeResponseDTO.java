package br.com.gathering.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionTypeResponseDTO {

	private Long id;
	private String name;

}
