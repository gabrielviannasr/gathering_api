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

import br.com.gathering.dto.RoundDTO;
import br.com.gathering.entity.Round;
import br.com.gathering.service.RoundService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/round")
public class RoundController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/round";
	private static final String ENTITY = "Round";

	@Autowired
	private RoundService service;

	@GetMapping
	public List<Round> getList(Round model) {
		LogHelper.info(log, RouteHelper.GET(PATH), ENTITY, model);
		return service.getList(model);
	}

	@GetMapping("/page")
	public Page<Round> getPage(Round model,
			@SortDefault.SortDefaults({ @SortDefault(sort = "idEvent"), @SortDefault(sort = "round") }) Sort sort,
			@RequestParam int page,
			@RequestParam int size) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/page"), "page", page, "size", size);
		return service.getPage(model, sort, page, size);
	}

	@GetMapping("{round}/event/{idEvent}")
	public Round getByRound(@PathVariable Long idEvent, @PathVariable Integer round) {
		LogHelper.info(log, RouteHelper.GET(PATH, "{round}/event/{idEvent}"), "round", round, "idEvent", idEvent);
		Round model = new Round();
		model.setIdEvent(idEvent);
		model.setRound(round);
		return service.getByRound(model);
	}
	
	@GetMapping("/{id}")
	public Round getById(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/{id}"), "id", id);
		return service.getById(id);
	}

	@PostMapping
	public Round save(@RequestBody RoundDTO dto) {
		Round model = dto.toModel();
		LogHelper.info(log, RouteHelper.POST(PATH), "payload", model);
		return service.save(model);
	}

	@PutMapping("/{id}")
	public Round update(@PathVariable Long id, @RequestBody RoundDTO dto) {
		Round model = dto.toModel();
		model.setId(id);
		LogHelper.info(log, RouteHelper.PUT(PATH), "id", id, "payload", model);
		return service.save(model);
	}

}
