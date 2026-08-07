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

import br.com.gathering.dto.request.EventDTO;
import br.com.gathering.entity.Event;
import br.com.gathering.service.EventService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/event")
public class EventController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/event";
	private static final String ENTITY = "Event";

	@Autowired
	private EventService service;

	@GetMapping
	public List<Event> getList(Event model) {
		LogHelper.info(log, RouteHelper.GET(PATH), ENTITY, model);
		return service.getList(model);
	}

	@GetMapping("/page")
	public Page<Event> getPage(Event model,
			@SortDefault.SortDefaults({ @SortDefault(sort = "idGathering"), @SortDefault(sort = "createdAt") }) Sort sort,
			@RequestParam int page,
			@RequestParam int size) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/page"), "page", page, "size", size);
		return service.getPage(model, sort, page, size);
	}

	@GetMapping("/{id}")
	public Event getById(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/{id}"), "id", id);
		return service.getById(id);
	}

	@PostMapping
	public Event create(@RequestBody EventDTO dto) {
		LogHelper.info(log, RouteHelper.POST(PATH), "payload", dto);
	    return service.create(dto.toModel());
	}

	@PutMapping("/{id}")
	public Event update(@PathVariable Long id, @RequestBody EventDTO dto) {
		LogHelper.info(log, RouteHelper.PUT(PATH, "/{id}"), "id", id, "payload", dto);
	    return service.update(id, dto);
	}

	@PostMapping("/{id}/finalize")
	public Event finalize(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.POST(PATH, "/{id}/finalize"), "id", id);
	    return service.finalize(id);
	}

	@PostMapping("/{id}/reopen")
	public Event reopen(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.POST(PATH, "/{id}/reopen"), "id", id);
	    return service.reopen(id);
	}

	@PostMapping("/{id}/cancel")
	public Event cancel(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.POST(PATH, "/{id}/cancel"), "id", id);
	    return service.cancel(id);
	}

	@PostMapping("/{id}/reactivate")
	public Event reactivate(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.POST(PATH, "/{id}/reactivate"), "id", id);
	    return service.reactivate(id);
	}

}
