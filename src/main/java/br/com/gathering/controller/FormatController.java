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

import br.com.gathering.dto.request.FormatDTO;
import br.com.gathering.entity.Format;
import br.com.gathering.service.FormatService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/format")
public class FormatController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/format";
	private static final String ENTITY = "Format";
	
	@Autowired
	private FormatService service;

	@GetMapping
	public List<Format> getList(Format model) {
		LogHelper.info(log, RouteHelper.GET(PATH), ENTITY, model);
		return service.getList(model);
	}

	@GetMapping("/page")
	public Page<Format> getPage(Format model,
			@SortDefault.SortDefaults({ @SortDefault(sort = "name") }) Sort sort,
			@RequestParam int page,
			@RequestParam int size) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/page"), "page", page, "size", size);
		return service.getPage(model, sort, page, size);
	}

	@GetMapping("/{id}")
	public Format getById(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/{id}"), "id", id);
		return service.getById(id);
	}

	@PostMapping
	public Format create(@RequestBody FormatDTO dto) {
		LogHelper.info(log, RouteHelper.POST(PATH), "dto", dto);
	    return service.create(dto);
	}

	@PutMapping("/{id}")
	public Format update(@PathVariable Long id, @RequestBody FormatDTO dto) {
		LogHelper.info(log, RouteHelper.PUT(PATH, "/{id}"), "id", id, "dto", dto);
	    return service.update(id, dto);
	}

}
