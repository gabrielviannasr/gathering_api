package br.com.gathering.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gathering.dto.request.ScoreDTO;
import br.com.gathering.entity.Score;
import br.com.gathering.service.ScoreService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/score")
public class ScoreController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/score";
	private static final String ENTITY = "Score";

	@Autowired
	private ScoreService service;

	@GetMapping
	public List<Score> getList(Score model) {
		LogHelper.info(log, RouteHelper.GET(PATH), ENTITY, model);
		return service.getList(model);
	}

	@GetMapping("/{id}")
	public Score getById(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/{id}"), "id", id);
		return service.getById(id);
	}

	@PostMapping
	public Score save(@RequestBody ScoreDTO dto) {
		Score model = dto.toModel();
		model.init();
		LogHelper.info(log, RouteHelper.POST(PATH), "payload", model);
		return service.save(model);
	}

	@PutMapping("/{id}")
	public Score update(@PathVariable Long id, @RequestBody ScoreDTO dto) {
		Score model = dto.toModel();
		model.setId(id);
		model.init();
		LogHelper.info(log, RouteHelper.PUT(PATH), "id", id, "payload", model);
		return service.save(model);
	}

}
