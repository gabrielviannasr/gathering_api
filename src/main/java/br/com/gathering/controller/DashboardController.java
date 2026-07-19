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

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	private static final Logger log = LogHelper.getLogger();

    @Autowired
    private DashboardService service;

    @GetMapping("/{idGathering}/format")
    public List<FormatProjection> getFormatProjection(@PathVariable Long idGathering) {
        LogHelper.info(log, "GET /format", "idGathering", idGathering);
        return service.getFormatProjection(idGathering);
    }

    @GetMapping("/{idGathering}/rank")
    public List<RankProjection> getRankProjection(@PathVariable Long idGathering) {
    	System.out.println("idGathering: " + idGathering);
    	LogHelper.info(log, "GET /rank", "idGathering", idGathering);
        return service.getRankProjection(idGathering);
    }

    @GetMapping("/{idGathering}/result")
    public List<GatheringResultResponseDTO> getResultProjection(@PathVariable Long idGathering) {
        System.out.println("idGathering: " + idGathering);
        LogHelper.info(log, "GET /result", "idGathering", idGathering);
        return service.getResultProjection(idGathering);
    }

    @GetMapping("/{idGathering}/summary")
    public GatheringSummaryResponseDTO getSummaryProjection(@PathVariable Long idGathering) {
        LogHelper.info(log, "GET /summary", "idGathering", idGathering);
        return service.getSummaryProjection(idGathering);
    }

    @GetMapping("/{idGathering}/transaction")
    public List<PlayerTransactionProjection> getPlayerTransaciton(@PathVariable Long idGathering) {
        LogHelper.info(log, "GET /transaction", "idGathering", idGathering);
        return service.getPlayerTransaciton(idGathering);
    }

    @GetMapping("/{idGathering}/wallet-balance")
    public List<PlayerWalletProjection> getWalletBalance(@PathVariable Long idGathering) {
        LogHelper.info(log, "GET /wallet-balance", "idGathering", idGathering);
        return service.getWalletBalance(idGathering);
    }

}
