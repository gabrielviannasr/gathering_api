package br.com.gathering.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.gathering.dto.response.FormatResponseDTO;
import br.com.gathering.dto.response.GatheringFormatResponseDTO;
import br.com.gathering.dto.response.GatheringResponseDTO;
import br.com.gathering.dto.response.GatheringResultResponseDTO;
import br.com.gathering.dto.response.GatheringSummaryResponseDTO;
import br.com.gathering.dto.response.GatheringWalletResponseDTO;
import br.com.gathering.dto.response.PlayerResponseDTO;
import br.com.gathering.dto.response.TransactionResponseDTO;
import br.com.gathering.dto.response.TransactionTypeResponseDTO;
import br.com.gathering.entity.Format;
import br.com.gathering.entity.Gathering;
import br.com.gathering.entity.Player;
import br.com.gathering.projection.RankProjection;
import br.com.gathering.projection.gathering.FormatProjection;
import br.com.gathering.projection.gathering.GatheringSummaryProjection;
import br.com.gathering.projection.gathering.PlayerTransactionProjection;
import br.com.gathering.projection.gathering.PlayerWalletProjection;
import br.com.gathering.projection.gathering.ResultProjection;
import br.com.gathering.repository.DashboardRepository;
import br.com.gathering.repository.GatheringRepository;
import br.com.gathering.util.LogHelper;

@Transactional(readOnly = true)
@Service
public class DashboardService {

    private static final Logger log = LogHelper.getLogger();

    @Autowired
    private DashboardRepository repository;
    
    @Autowired
    private GatheringRepository gatheringRepository;

    public List<GatheringWalletResponseDTO> getWallets(Long idGathering) {
        LogHelper.info(log, "Fetching wallet balance", "idGathering", idGathering);
        List<PlayerWalletProjection> list = repository.getWalletBalance(idGathering);
        LogHelper.info(log, "Fetched wallet balance", "count", list.size());

        return list.stream()
        		.map(this::buildGatheringWalletResponse)
        		.toList();
    }
    
    public GatheringWalletResponseDTO getWallet(Long idGathering, Long idPlayer) {

        LogHelper.info(log, "Fetching wallet balance",
            "idGathering", idGathering,
            "idPlayer", idPlayer);

        PlayerWalletProjection result = repository
            .getWalletBalance(idGathering, idPlayer)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Carteira do jogador não encontrado"));

        return buildGatheringWalletResponse(result);
    }

    private GatheringWalletResponseDTO buildGatheringWalletResponse(PlayerWalletProjection item) {
    	return GatheringWalletResponseDTO.builder()
    			.idPlayer(item.getIdPlayer())
    			.player(
    					PlayerResponseDTO.builder()
    					.id(item.getIdPlayer())
    					.name(item.getPlayerName())
    					.build())
    			.wallet(item.getWallet())
    			.build();
    }

    public List<TransactionResponseDTO> getTransactions(Long idGathering) {
        LogHelper.info(log, "Fetching player transactions", "idGathering", idGathering);
        List<PlayerTransactionProjection> list = repository.getTransactions(idGathering);
        LogHelper.info(log, "Fetched player transactions", "count", list.size());
        return list.stream()
        		.map(this::buildTransactionResponse)
        		.toList();
    }

    private TransactionResponseDTO buildTransactionResponse(PlayerTransactionProjection item) {
    	return TransactionResponseDTO.builder()
    			.id(item.getIdTransaction())
    			.amount(item.getAmount())
    			.createdAt(item.getCreatedAt())
    			.description(item.getTransactionDescription())
    			.player(
					PlayerResponseDTO.builder()
						.id(item.getIdPlayer())
    					.name(item.getPlayerName())
    					.build())
    			.type(
					TransactionTypeResponseDTO.builder()
    					.id(item.getIdTransactionType())
    					.name(item.getTransactionTypeName())
    					.build())
    			.build();
    }

    public List<GatheringFormatResponseDTO> getGatheringFormats(Long idGathering) {
        LogHelper.info(log, "Fetching formats", "idGathering", idGathering);
        List<FormatProjection> list = repository.getFormatProjection(idGathering);
        LogHelper.info(log, "Fetched formats", "count", list.size());

        int maxNameLength = list.stream()
                .map(FormatProjection::getFormatName)
                .filter(Objects::nonNull)
                .mapToInt(String::length)
                .max()
                .orElse(Format.NAME_LENGTH);

        // Logging details in formatted table style
        String format = "{ formatName: %-" + maxNameLength + "s | rounds: %3d }";
        list.forEach(item -> 
            log.info(String.format(
                format,
                item.getFormatName(),
                item.getRounds()
            ))
        );
        return list.stream()
        	    .map(this::buildGatheringFormatResponse)
        	    .toList();
    }

    private GatheringFormatResponseDTO buildGatheringFormatResponse(FormatProjection item) {
        return GatheringFormatResponseDTO.builder()
            .idFormat(item.getIdFormat())
            .format(
                FormatResponseDTO.builder()
                    .id(item.getIdFormat())
                    .name(item.getFormatName())
                    .build()
            )
            .rounds(item.getRounds())
            .build();
    }
    
    public List<RankProjection> getRankProjection(Long idGathering) {
        LogHelper.info(log, "Fetching player ranking", "idGathering", idGathering);
        List<RankProjection> list = repository.getRankProjection(idGathering);

        int maxNameLength = list.stream()
            .map(RankProjection::getPlayerName)
            .filter(Objects::nonNull)
            .mapToInt(String::length)
            .max()
            .orElse(Player.NAME_LENGTH);

        LogHelper.info(log, "Fetched player ranking", "count", list.size(), "maxNameLength", maxNameLength);

        // Logging details in formatted table style
        String format = "{ rank: %-2d | name: %-" + maxNameLength + "s | rankBalance: %8.2f }";
        list.forEach(item -> 
            log.info(String.format(
                format,
                item.getRank(),
                item.getPlayerName(),
                item.getRankBalance()
            ))
        );

        return list;
    }

    public List<GatheringResultResponseDTO> getGatheringResults(Long idGathering) {
        LogHelper.info(log, "Fetching result ranking", "idGathering", idGathering);
        List<ResultProjection> list = repository.getResultProjection(idGathering);

        int maxNameLength = list.stream()
            .map(RankProjection::getPlayerName)
            .filter(Objects::nonNull)
            .mapToInt(String::length)
            .max()
            .orElse(Player.NAME_LENGTH);

        LogHelper.info(log, "Fetched result ranking", "count", list.size(), "maxNameLength", maxNameLength);

        // Logging details in formatted table style
        String format = "{ rank: %-2d | name: %-" + maxNameLength + "s | finalBalance: %8.2f }";
        list.forEach(item -> 
            log.info(String.format(
                format,
                item.getRank(),
                item.getPlayerName(),
                item.getFinalBalance()
            ))
        );


		return list.stream()
		    .map(this::buildGatheringResultResponse)
		    .toList();
    }

    public GatheringResultResponseDTO getGatheringResult(Long idGathering, Long idPlayer) {

        LogHelper.info(log, "Fetching player result",
            "idGathering", idGathering,
            "idPlayer", idPlayer);

        ResultProjection result = repository
            .getResultProjection(idGathering, idPlayer)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Resultado do jogador não encontrado"));

        return buildGatheringResultResponse(result);
    }

    private GatheringResultResponseDTO buildGatheringResultResponse(ResultProjection item) {
        return GatheringResultResponseDTO.builder()
            .idPlayer(item.getIdPlayer())
    		.player(
                Player.builder()
                    .id(item.getIdPlayer())
                    .name(item.getPlayerName())
                    .build()
            )
            .rank(item.getRank())
            .events(item.getEvents())
            .wins(item.getWins())
            .rounds(item.getRounds())
            .positive(item.getPositive())
            .negative(item.getNegative())
            .rankBalance(item.getRankBalance())
            .loserPot(item.getLoserPot())
            .confraPot(item.getConfraPot())
            .finalBalance(item.getFinalBalance())
            .build();
    }

    public GatheringSummaryResponseDTO getSummaryProjection(Long idGathering) {
        LogHelper.info(log, "Fetching summary", "idGathering", idGathering);
        GatheringSummaryProjection summary = repository.getSummaryProjection(idGathering);
        if (summary == null) {
            LogHelper.warn(log, "No summary found for gathering", "idGathering", idGathering);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resumo da confra não encontrado");
        } else {
            LogHelper.info(log, "Fetched summary successfully", "idGathering", idGathering);
        }
//        return summary;

        Gathering gathering = gatheringRepository.findById(idGathering)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Confra não encontrada"));

        return  GatheringSummaryResponseDTO.builder()
    	    .idGathering(summary.getIdGathering())
    	    .gathering(
	    	    GatheringResponseDTO.builder()
	    	        .id(gathering.getId())
	    	        .name(gathering.getName())
	    	        .year(gathering.getYear())
	    	        .build()
	    	)
    	    .events(summary.getEvents())
    	    .players(summary.getPlayers())
    	    .rounds(summary.getRounds())
    	    .loserPot(summary.getLoserPot())
    	    .confraPot(summary.getConfraPot())
    	    .prize(summary.getPrize())
    	    .build();
    }

}
