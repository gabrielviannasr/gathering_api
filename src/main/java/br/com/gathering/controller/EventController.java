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
import br.com.gathering.dto.response.EventResponseDTO;
import br.com.gathering.entity.Event;
import br.com.gathering.mapper.EventMapper;
import br.com.gathering.service.EventService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/event")
public class EventController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/event";

	@Autowired
	private EventService service;

	@Autowired
	private EventMapper mapper;

	@GetMapping
	public List<EventResponseDTO> getList(Event model) {
		LogHelper.info(log, RouteHelper.GET(PATH), "model", model);
		return service.getList(model)
				.stream()
	            .map(mapper::toResponse)
	            .toList();
	}

	@GetMapping("/page")
	public Page<EventResponseDTO> getPage(Event model,
			@SortDefault.SortDefaults({ @SortDefault(sort = "idGathering"), @SortDefault(sort = "createdAt") }) Sort sort,
			@RequestParam int page,
			@RequestParam int size) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/page"), "page", page, "size", size);
		return service.getPage(model, sort, page, size)
				.map(mapper::toResponse);
	}

	@GetMapping("/{id}")
	public EventResponseDTO getById(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/{id}"), "id", id);
		return mapper.toResponse(service.getById(id));
	}

	@PostMapping
	public EventResponseDTO create(@RequestBody EventDTO dto) {
		LogHelper.info(log, RouteHelper.POST(PATH), "dto", dto);
	    return mapper.toResponse(service.create(dto));
	}

	@PutMapping("/{id}")
	public EventResponseDTO update(@PathVariable Long id, @RequestBody EventDTO dto) {
		LogHelper.info(log, RouteHelper.PUT(PATH, "/{id}"), "id", id, "dto", dto);
	    return mapper.toResponse(service.update(id, dto));
	}

	@PostMapping("/{id}/cancel")
	public EventResponseDTO cancel(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.POST(PATH, "/{id}/cancel"), "id", id);
	    return mapper.toResponse(service.cancel(id));
	}

	@PostMapping("/{id}/finalize")
	public EventResponseDTO finalize(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.POST(PATH, "/{id}/finalize"), "id", id);
	    return mapper.toResponse(service.finalize(id));
	}

	@PostMapping("/{id}/reactivate")
	public EventResponseDTO reactivate(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.POST(PATH, "/{id}/reactivate"), "id", id);
	    return mapper.toResponse(service.reactivate(id));
	}

	@PostMapping("/{id}/reopen")
	public EventResponseDTO reopen(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.POST(PATH, "/{id}/reopen"), "id", id);
	    return mapper.toResponse(service.reopen(id));
	}	

}
