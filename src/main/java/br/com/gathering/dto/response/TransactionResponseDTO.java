package br.com.gathering.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionResponseDTO {

	private Long id;
	private Double amount;
	private LocalDateTime createdAt;
	private TransactionTypeResponseDTO type;
	private PlayerResponseDTO player;

}
