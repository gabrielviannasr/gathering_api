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

import br.com.gathering.dto.request.GatheringDTO;
import br.com.gathering.entity.Gathering;
import br.com.gathering.service.GatheringService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/gathering")
public class GatheringController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/gathering";
	
	@Autowired
	private GatheringService service;

	@GetMapping
	public List<Gathering> getList(Gathering model) {
		LogHelper.info(log, RouteHelper.GET(PATH), "model", model);
		return service.getList(model);
	}

	@GetMapping("/page")
	public Page<Gathering> getPage(Gathering model,
			@SortDefault.SortDefaults({ @SortDefault(sort = "name") }) Sort sort,
			@RequestParam int page,
			@RequestParam int size) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/page"), "page", page, "size", size);
		return service.getPage(model, sort, page, size);
	}

	@GetMapping("/{id}")
	public Gathering getById(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/{id}"), "id", id);
		return service.getById(id);
	}

	@PostMapping
	public Gathering create(@RequestBody GatheringDTO dto) {
		LogHelper.info(log, RouteHelper.POST(PATH), "dto", dto);
	    return service.create(dto);
	}

	@PutMapping("/{id}")
	public Gathering update(@PathVariable Long id, @RequestBody GatheringDTO dto) {
		LogHelper.info(log, RouteHelper.PUT(PATH, "/{id}"), "id", id, "dto", dto);
	    return service.update(id, dto);
	}

	@GetMapping("/year")
	public List<Integer> getYears() {
	    LogHelper.info(log, RouteHelper.GET(PATH, "/year"));
	    return service.getYears();
	}

}
