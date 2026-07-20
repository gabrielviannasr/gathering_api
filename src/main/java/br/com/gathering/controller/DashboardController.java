package br.com.gathering.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gathering.dto.response.GatheringFormatResponseDTO;
import br.com.gathering.dto.response.GatheringResultResponseDTO;
import br.com.gathering.dto.response.GatheringSummaryResponseDTO;
import br.com.gathering.projection.RankProjection;
import br.com.gathering.projection.gathering.PlayerTransactionProjection;
import br.com.gathering.projection.gathering.PlayerWalletProjection;
import br.com.gathering.service.DashboardService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/dashboard";

    @Autowired
    private DashboardService service;

    @GetMapping("/format/{idGathering}")
    public List<GatheringFormatResponseDTO> getFormatProjection(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/format/{idGathering}"), "idGathering", idGathering);
        return service.getGatheringFormats(idGathering);
    }

    @GetMapping("/rank/{idGathering}")
    public List<RankProjection> getRankProjection(@PathVariable Long idGathering) {
    	LogHelper.info(log, RouteHelper.GET(PATH + "/rank/{idGathering}"), "idGathering", idGathering);
        return service.getRankProjection(idGathering);
    }
 
    @GetMapping("/result/{idGathering}")
    public List<GatheringResultResponseDTO> getGatheringResults(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/result/{idGathering}"), "idGathering", idGathering);
        return service.getGatheringResults(idGathering);
    }

    @GetMapping("/result/{idGathering}/player/{idPlayer}")
    public GatheringResultResponseDTO getGatheringResult(@PathVariable Long idGathering, @PathVariable Long idPlayer) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/result/{idGathering}/player/{idPlayer}"), "idGathering", idGathering, "idPlayer", idPlayer);
        return service.getGatheringResult(idGathering, idPlayer);
    }

    @GetMapping("/summary/{idGathering}")
    public GatheringSummaryResponseDTO getSummaryProjection(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/summary/{idGathering}"), "idGathering", idGathering);
        return service.getSummaryProjection(idGathering);
    }

    @GetMapping("/transaction/{idGathering}")
    public List<PlayerTransactionProjection> getPlayerTransaciton(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/transaction/{idGathering}"), "idGathering", idGathering);
        return service.getPlayerTransaciton(idGathering);
    }

    @GetMapping("/wallet-balance/{idGathering}")
    public List<PlayerWalletProjection> getWalletBalance(@PathVariable Long idGathering) {
        LogHelper.info(log, RouteHelper.GET(PATH + "/wallet-balance/{idGathering}"), "idGathering", idGathering);
        return service.getWalletBalance(idGathering);
    }

}
