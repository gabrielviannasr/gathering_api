package br.com.gathering.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gathering.dto.request.RoundDTO;
import br.com.gathering.entity.Round;
import br.com.gathering.service.RoundService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/event/{idEvent}/round")
public class RoundController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/event/{idEvent}/round";

	@Autowired
	private RoundService service;

	@GetMapping
	public List<Round> getList(@PathVariable Long idEvent, Round model) {
		LogHelper.info(log, RouteHelper.GET(PATH), "idEvent", idEvent, "model", model);
		return service.getList(idEvent, model);
	}

	@GetMapping("/page")
	public Page<Round> getPage(@PathVariable Long idEvent,
			Round model,
			@SortDefault.SortDefaults({ @SortDefault(sort = "idEvent"), @SortDefault(sort = "round") }) Sort sort,
			@RequestParam int page,
			@RequestParam int size) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/page"),  "idEvent", idEvent, "page", page, "size", size);
		return service.getPage(idEvent, model, sort, page, size);
	}

	@GetMapping("/id/{idRound}")
	public Round getById(@PathVariable Long idRound) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/id/{idRound}"), "idRound", idRound);
		return service.getById(idRound);
	}

	@GetMapping("/{round}")
	public Round getByIdEventAndRound(@PathVariable Long idEvent, @PathVariable Integer round) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/{round}"), "idEvent", idEvent, "round", round);
		return service.getByIdEventAndRound(idEvent, round);
	}

	@PostMapping
	public Round create(@PathVariable Long idEvent, @RequestBody RoundDTO dto) {
		LogHelper.info(log, RouteHelper.POST(PATH), "idEvent", idEvent, "dto", dto);
	    return service.create(idEvent, dto);
	}

	@PutMapping("/{round}")
	public Round update(@PathVariable Long idEvent, @PathVariable Integer round, @RequestBody RoundDTO dto) {
		LogHelper.info(log, RouteHelper.PUT(PATH, "/{round}"), "idEvent", idEvent, "round", round, "dto", dto);
	    return service.update(idEvent, round, dto);
	}

}
