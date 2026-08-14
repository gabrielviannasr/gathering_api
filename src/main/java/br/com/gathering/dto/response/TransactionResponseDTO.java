package br.com.gathering.dto.response;

import java.time.LocalDateTime;

import br.com.gathering.entity.Transaction;
import br.com.gathering.projection.gathering.PlayerTransactionProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionResponseDTO {

	private Long id;
	private Double amount;
	private LocalDateTime createdAt;
	private String description;
	private TransactionTypeResponseDTO type;
	private PlayerResponseDTO player;
	private EventResponseDTO event;
	private GatheringResponseDTO gathering;

	public static TransactionResponseDTO from(Transaction model) {
		return TransactionResponseDTO.builder()
				.id(model.getId())
	            .amount(model.getAmount())
	            .createdAt(model.getCreatedAt())
	            .description(model.getDescription())
	            .type(
	            		TransactionTypeResponseDTO.builder()
	            		.id(model.getType().getId())
	                    .name(model.getType().getName())
	                    .description(model.getType().getDescription())
	                    .build())
	                .player(
	                		PlayerResponseDTO.builder()
	                        .id(model.getPlayer().getId())
	                        .name(model.getPlayer().getName())
	                        .build())
	                .event(
	                		model.getEvent() == null ? null :
	                        EventResponseDTO.builder()
	                        .id(model.getEvent().getId())
	                        .createdAt(model.getEvent().getCreatedAt())
	                        .format(
	                        		model.getEvent().getFormat() == null?
	                        		null:
		                            FormatResponseDTO.builder()
		                            .id(model.getEvent().getFormat().getId())
		                            .name(model.getEvent().getFormat().getName())
		                            .build())
	                        .build())
	                .gathering(
	                		GatheringResponseDTO.builder()
	                        .id(model.getGathering().getId())
	                        .name(model.getGathering().getName())
	                        .year(model.getGathering().getYear())
	                        .build())
	                .build();
		}

	public static TransactionResponseDTO from(PlayerTransactionProjection model) {
		return TransactionResponseDTO.builder()
				.id(model.getIdTransaction())
	            .amount(model.getAmount())
	            .createdAt(model.getCreatedAt())
	            .description(model.getTransactionDescription())
	            .player(
	            		PlayerResponseDTO.builder()
	                    .id(model.getIdPlayer())
	                    .name(model.getPlayerName())
	                    .build())
	            .type(
	            		TransactionTypeResponseDTO.builder()
	                    .id(model.getIdTransactionType())
	                    .name(model.getTransactionTypeName())
	                    .build())
	            .build();
	    }

}
