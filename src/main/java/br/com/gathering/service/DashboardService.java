package br.com.gathering.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.gathering.dto.response.GatheringFormatResponseDTO;
import br.com.gathering.dto.response.GatheringResponseDTO;
import br.com.gathering.dto.response.GatheringResultResponseDTO;
import br.com.gathering.dto.response.GatheringSummaryResponseDTO;
import br.com.gathering.dto.response.GatheringSummaryResponseDTO.GatheringSummaryResponseDTOBuilder;
import br.com.gathering.dto.response.GatheringWalletResponseDTO;
import br.com.gathering.dto.response.TransactionResponseDTO;
import br.com.gathering.entity.Format;
import br.com.gathering.entity.Gathering;
import br.com.gathering.entity.Player;
import br.com.gathering.mapper.GatheringFormatResponseMapper;
import br.com.gathering.mapper.GatheringResultResponseMapper;
import br.com.gathering.mapper.GatheringWalletResponseMapper;
import br.com.gathering.mapper.TransactionResponseMapper;
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

        LogHelper.info(log, "Fetching wallet balance list", "idGathering", idGathering);

        List<PlayerWalletProjection> list = repository.getWalletBalance(idGathering);

        LogHelper.info(log, "Fetched wallet balance list", "count", list.size());

        return list.stream()
        		.map(GatheringWalletResponseMapper::from)
        		.toList();
    }
    
    public GatheringWalletResponseDTO getWallet(Long idGathering, Long idPlayer) {

        LogHelper.info(log, "Fetching wallet balance by idGathering and idPlayer", "idGathering", idGathering, "idPlayer", idPlayer);

        PlayerWalletProjection found = repository.getWalletBalance(idGathering, idPlayer)
            .orElseThrow(() -> new ResponseStatusException(
            		HttpStatus.NOT_FOUND,
            		"Carteira do jogador não encontrado"));

        LogHelper.info(log, "Found", "idGathering", idGathering, "idPlayer", idPlayer);

        return GatheringWalletResponseMapper.from(found);
    }

    public List<TransactionResponseDTO> getTransactions(Long idGathering) {

        LogHelper.info(log, "Fetching transaction list", "idGathering", idGathering);

        List<PlayerTransactionProjection> list = repository.getTransactions(idGathering);

        LogHelper.info(log, "Fetched transaction list", "count", list.size());

        return list.stream()
        		.map(TransactionResponseMapper::from)
        		.toList();
    }

    public List<GatheringFormatResponseDTO> getGatheringFormats(Long idGathering) {

        LogHelper.info(log, "Fetching format list", "idGathering", idGathering);

        List<FormatProjection> list = repository.getFormatProjection(idGathering);

        LogHelper.info(log, "Fetched format list", "count", list.size());

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
        	    .map(GatheringFormatResponseMapper::from)
        	    .toList();
    }
    
    public List<RankProjection> getRankProjection(Long idGathering) {

        LogHelper.info(log, "Fetching gathering ranking list", "idGathering", idGathering);

        List<RankProjection> list = repository.getRankProjection(idGathering);

        int maxNameLength = list.stream()
            .map(RankProjection::getPlayerName)
            .filter(Objects::nonNull)
            .mapToInt(String::length)
            .max()
            .orElse(Player.NAME_LENGTH);

        LogHelper.info(log, "Fetched gathering ranking list", "count", list.size(), "maxNameLength", maxNameLength);

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

        LogHelper.info(log, "Fetching gathering result list", "idGathering", idGathering);

        List<ResultProjection> list = repository.getResultProjection(idGathering);

        int maxNameLength = list.stream()
            .map(RankProjection::getPlayerName)
            .filter(Objects::nonNull)
            .mapToInt(String::length)
            .max()
            .orElse(Player.NAME_LENGTH);

        LogHelper.info(log, "Fetched gathering result list", "count", list.size(), "maxNameLength", maxNameLength);

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
		    .map(GatheringResultResponseMapper::from)
		    .toList();
    }

    public GatheringResultResponseDTO getGatheringResult(Long idGathering, Long idPlayer) {

        LogHelper.info(log, "Fetching gathering result", "idGathering", idGathering, "idPlayer", idPlayer);

        ResultProjection result = repository
            .getResultProjection(idGathering, idPlayer)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Resultado do jogador não encontrado"));

        LogHelper.info(log, "Fetched gathering result", "idGathering", idGathering, "idPlayer", idPlayer);

        return GatheringResultResponseMapper.from(result);
    }

    public GatheringSummaryResponseDTO getSummaryProjection(Long idGathering) {

        LogHelper.info(log, "Fetching gathering summary", "idGathering", idGathering);
        
        Gathering gathering = gatheringRepository.findById(idGathering)
        		.orElseThrow(() -> new ResponseStatusException(
        				HttpStatus.NOT_FOUND, "Gathering not found"));
        
        GatheringSummaryProjection summary = repository.getSummaryProjection(idGathering);

        GatheringSummaryResponseDTOBuilder builder = 
        		GatheringSummaryResponseDTO.builder()
	        		.idGathering(idGathering)
	    		   	.gathering(
	    		   		GatheringResponseDTO.builder()
	    		   			.id(gathering.getId())
	    		   			.name(gathering.getName())
	    		   			.year(gathering.getYear())
	    		   			.build()
	    );
        
        if (summary == null) {
        	LogHelper.info(log, "Summary not found. Returning empty summary", "idGathering", idGathering);

            return builder
        	    .events(0)
        	    .players(0)
        	    .rounds(0)
        	    .loserPot(0.0)
        	    .confraPot(0.0)
        	    .prize(0.0)                	
        		.build();

        } else {
            LogHelper.info(log, "Fetched summary successfully", "idGathering", idGathering);
        }

        return  builder
    	    .events(summary.getEvents())
    	    .players(summary.getPlayers())
    	    .rounds(summary.getRounds())
    	    .loserPot(summary.getLoserPot())
    	    .confraPot(summary.getConfraPot())
    	    .prize(summary.getPrize())
    	    .build();
    }

}
