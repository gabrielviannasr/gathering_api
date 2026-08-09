package br.com.gathering.mapper;

import br.com.gathering.dto.response.GatheringWalletResponseDTO;
import br.com.gathering.dto.response.PlayerResponseDTO;
import br.com.gathering.projection.gathering.PlayerWalletProjection;

public final class GatheringWalletResponseMapper {

    public static GatheringWalletResponseDTO from(PlayerWalletProjection model) {
        return GatheringWalletResponseDTO.builder()
                .idPlayer(model.getIdPlayer())
                .player(
                        PlayerResponseDTO.builder()
                        	.id(model.getIdPlayer())
                            .name(model.getPlayerName())
                            .build())
                .wallet(model.getWallet())
                .build();
    }

}