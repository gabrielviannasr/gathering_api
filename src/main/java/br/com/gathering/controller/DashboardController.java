package br.com.gathering.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gathering.dto.response.GatheringResultResponseDTO;
import br.com.gathering.dto.response.GatheringSummaryResponseDTO;
import br.com.gathering.projection.RankProjection;
import br.com.gathering.projection.gathering.FormatProjection;
import br.com.gathering.projection.gathering.PlayerTransactionProjection;
import br.com.gathering.projection.gathering.PlayerWalletProjection;
import br.com.gathering.service.DashboardService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/dashboard/{idGathering}")
public class DashboardController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/dashboard/{idGathering}";

    @Autowired
    private DashboardService service;

    @GetMapping("/format")
    public List<FormatProjection> getFormatProjection(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/format"), "idGathering", idGathering);
        return service.getFormatProjection(idGathering);
    }

    @GetMapping("/rank")
    public List<RankProjection> getRankProjection(@PathVariable Long idGathering) {
    	LogHelper.info(log, RouteHelper.GET(PATH + "/rank"), "idGathering", idGathering);
        return service.getRankProjection(idGathering);
    }
 
    @GetMapping("/result")
    public List<GatheringResultResponseDTO> getGatheringResults(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/result"), "idGathering", idGathering);
        return service.getGatheringResults(idGathering);
    }

    @GetMapping("/result/player/{idPlayer}")
    public GatheringResultResponseDTO getGatheringResult(@PathVariable Long idGathering, @PathVariable Long idPlayer) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/result/player/{idPlayer}"), "idGathering", idGathering, "idPlayer", idPlayer);
        return service.getGatheringResult(idGathering, idPlayer);
    }

    @GetMapping("/summary")
    public GatheringSummaryResponseDTO getSummaryProjection(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/summary"), "idGathering", idGathering);
        return service.getSummaryProjection(idGathering);
    }

    @GetMapping("/transaction")
    public List<PlayerTransactionProjection> getPlayerTransaciton(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/transaction"), "idGathering", idGathering);
        return service.getPlayerTransaciton(idGathering);
    }

    @GetMapping("/wallet-balance")
    public List<PlayerWalletProjection> getWalletBalance(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/wallet-balance"), "idGathering", idGathering);
        return service.getWalletBalance(idGathering);
    }

}
