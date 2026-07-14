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

import br.com.gathering.dto.GatheringDTO;
import br.com.gathering.entity.Gathering;
import br.com.gathering.service.GatheringService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/gathering")
public class GatheringController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/gathering";
	private static final String ENTITY = "Gathering";
	
	@Autowired
	private GatheringService service;

	@GetMapping
	public List<Gathering> getList(Gathering model) {
		LogHelper.info(log, RouteHelper.GET(PATH), ENTITY, model);
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
	public Gathering save(@RequestBody GatheringDTO dto) {
		Gathering model = dto.toModel();
		LogHelper.info(log, RouteHelper.POST(PATH), "payload", model);
		return service.save(model); 
	}

	@PutMapping("/{id}")
	public Gathering update(@PathVariable Long id, @RequestBody GatheringDTO dto) {
		Gathering model = dto.toModel();
		model.setId(id);
		LogHelper.info(log, RouteHelper.PUT(PATH), "id", id, "payload", model);
		return service.save(model);
	}
	@GetMapping("/year")
	public List<Integer> getYears() {
	    LogHelper.info(log, RouteHelper.GET(PATH, "/year"));
	    return service.getYears();
	}
}
