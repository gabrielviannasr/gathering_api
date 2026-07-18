package br.com.gathering.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.gathering.dto.GatheringResultDTO;
import br.com.gathering.entity.Format;
import br.com.gathering.entity.Player;
import br.com.gathering.projection.RankProjection;
import br.com.gathering.projection.gathering.FormatProjection;
import br.com.gathering.projection.gathering.GatheringSummaryProjection;
import br.com.gathering.projection.gathering.PlayerTransactionProjection;
import br.com.gathering.projection.gathering.PlayerWalletProjection;
import br.com.gathering.projection.gathering.ResultProjection;
import br.com.gathering.repository.DashboardRepository;
import br.com.gathering.util.LogHelper;

@Transactional(readOnly = true)
@Service
public class DashboardService {

    private static final Logger log = LogHelper.getLogger();

    @Autowired
    private DashboardRepository repository;

    public List<PlayerWalletProjection> getWalletBalance(Long idGathering) {
        LogHelper.info(log, "Fetching wallet balance", "idGathering", idGathering);
        List<PlayerWalletProjection> list = repository.getWalletBalance(idGathering);
        LogHelper.info(log, "Fetched wallet balance", "count", list.size());
        return list;
    }

    public List<PlayerTransactionProjection> getPlayerTransaciton(Long idGathering) {
        LogHelper.info(log, "Fetching player transactions", "idGathering", idGathering);
        List<PlayerTransactionProjection> list = repository.getPlayerTransaciton(idGathering);
        LogHelper.info(log, "Fetched player transactions", "count", list.size());
        return list;
    }

    public List<FormatProjection> getFormatProjection(Long idGathering) {
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
        return list;
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

    public List<GatheringResultDTO> getResultProjection(Long idGathering) {
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
		    .map(this::toDTO)
		    .toList();
    }

    private GatheringResultDTO toDTO(ResultProjection item) {
        return GatheringResultDTO.builder()
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

    public GatheringSummaryProjection getSummaryProjection(Long idGathering) {
        LogHelper.info(log, "Fetching summary", "idGathering", idGathering);
        GatheringSummaryProjection summary = repository.getSummaryProjection(idGathering);
        if (summary == null) {
            LogHelper.warn(log, "No summary found for gathering", "idGathering", idGathering);
        } else {
            LogHelper.info(log, "Fetched summary successfully", "idGathering", idGathering);
        }
        return summary;
    }

}
