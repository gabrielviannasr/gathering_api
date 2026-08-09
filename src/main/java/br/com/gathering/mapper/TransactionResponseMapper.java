package br.com.gathering.mapper;

import br.com.gathering.dto.response.EventResponseDTO;
import br.com.gathering.dto.response.FormatResponseDTO;
import br.com.gathering.dto.response.GatheringResponseDTO;
import br.com.gathering.dto.response.PlayerResponseDTO;
import br.com.gathering.dto.response.TransactionResponseDTO;
import br.com.gathering.dto.response.TransactionTypeResponseDTO;
import br.com.gathering.entity.Transaction;
import br.com.gathering.projection.gathering.PlayerTransactionProjection;

public final class TransactionResponseMapper {

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