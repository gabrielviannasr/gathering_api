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
	private static final String ENTITY = "Round";

	@Autowired
	private RoundService service;

	@GetMapping
	public List<Round> getList(@PathVariable Long idEvent, Round model) {
		model.setIdEvent(idEvent);
		LogHelper.info(log, RouteHelper.GET(PATH), ENTITY, model);
		return service.getList(model);
	}

	@GetMapping("/page")
	public Page<Round> getPage(@PathVariable Long idEvent,
			Round model,
			@SortDefault.SortDefaults({ @SortDefault(sort = "idEvent"), @SortDefault(sort = "round") }) Sort sort,
			@RequestParam int page,
			@RequestParam int size) {
		model.setIdEvent(idEvent);
		LogHelper.info(log, RouteHelper.GET(PATH, "/page"), "page", page, "size", size);
		return service.getPage(model, sort, page, size);
	}

	@GetMapping("/{round}")
	public Round getByRound(@PathVariable Long idEvent, @PathVariable Integer round) {
		LogHelper.info(log, RouteHelper.GET(PATH, "{round}"), "round", round, "idEvent", idEvent);
		Round model = new Round();
		model.setIdEvent(idEvent);
		model.setRound(round);
		return service.getByRound(model);
	}

	@GetMapping("/id/{idRound}")
	public Round getById(@PathVariable Long idRound) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/id/{idRound}"), "idRound", idRound);
		return service.getById(idRound);
	}

	@PostMapping
	public Round save(@PathVariable Long idEvent, @RequestBody RoundDTO dto) {
		Round model = dto.toModel();
		model.setIdEvent(idEvent);
		LogHelper.info(log, RouteHelper.POST(PATH), "payload", model);
		return service.save(model);
	}

	@PutMapping("/{round}")
	public Round update(@PathVariable Long idEvent, @PathVariable Integer round, @RequestBody RoundDTO dto) {
		Round model = dto.toModel();
		model.setIdEvent(idEvent);
		model.setRound(round);
		LogHelper.info(log, RouteHelper.PUT(PATH, "/{round}"), "round", round, "payload", model);
		return service.update(model);
	}

}
