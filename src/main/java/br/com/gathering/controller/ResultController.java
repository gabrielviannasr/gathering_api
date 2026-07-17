package br.com.gathering.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gathering.entity.Result;
import br.com.gathering.projection.RankProjection;
import br.com.gathering.projection.event.ConfraPotProjection;
import br.com.gathering.projection.event.EventSummaryProjection;
import br.com.gathering.projection.event.LoserPotProjection;
import br.com.gathering.projection.event.RankCountProjection;
import br.com.gathering.service.ResultService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/result")
public class ResultController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/result";
	@SuppressWarnings("unused")
	private static final String ENTITY = "Result";

    @Autowired
    private ResultService service;

    @GetMapping("/{idEvent}")
    public List<Result> getResult(@PathVariable Long idEvent) {
    	System.out.println("idEvent: " + idEvent);
    	LogHelper.info(log, RouteHelper.GET(PATH, "/{idEvent}"), "idEvent", idEvent);
        return service.getResult(idEvent);
    }

//    @GetMapping("/{idEvent}/player/{idPlayer}")
//    public Result getResult(@PathVariable Long idEvent, @PathVariable Long idPlayer) {
//    	System.out.println("idEvent: " + idEvent);
//    	LogHelper.info(log, RouteHelper.GET(PATH, "/{idEvent}/player/{idPlayer}"), "idEvent", idEvent, "idPlayer", idPlayer);
//        return service.getResult(idEvent, idPlayer);
//    }

//    @PostMapping("/{idEvent}")
//    public List<Result> saveResult(@PathVariable Long idEvent) {
//    	LogHelper.info(log, RouteHelper.POST(PATH, "/{idEvent}"), "idEvent", idEvent);
//        return service.saveResult(idEvent);
//    }

    @GetMapping("/{idEvent}/confra-pot")
    public ConfraPotProjection getConfraPot(@PathVariable Long idEvent) {
    	LogHelper.info(log, RouteHelper.GET(PATH, "/{idEvent}/confra-pot"), "idEvent", idEvent);
        return service.getConfraPot(idEvent);
    }

    @GetMapping("/{idEvent}/loser-pot")
    public LoserPotProjection getLoserPot(@PathVariable Long idEvent) {
    	LogHelper.info(log, RouteHelper.GET(PATH, "/{idEvent}/loser-pot"), "idEvent", idEvent);
        return service.getLoserPot(idEvent);
    }

    @GetMapping("/{idEvent}/rank-count")
    public List<RankCountProjection> getRankCount(@PathVariable Long idEvent) {
    	LogHelper.info(log, RouteHelper.GET(PATH, "/{idEvent}/rank-count"), "idEvent", idEvent);
        return service.getRankCount(idEvent);
    }

    @GetMapping("/{idEvent}/rank")
    public List<RankProjection> getRankProjection(@PathVariable Long idEvent) {
    	LogHelper.info(log, RouteHelper.GET(PATH, "/{idEvent}/rank"), "idEvent", idEvent);
        return service.getRankProjection(idEvent);
    }
    
    @GetMapping("/{idEvent}/summary")
    public EventSummaryProjection getSummaryProjection(@PathVariable Long idEvent) {
    	LogHelper.info(log, RouteHelper.GET(PATH, "/{idEvent}/summary"), "idEvent", idEvent);
        return service.getSummaryProjection(idEvent);
    }

}
