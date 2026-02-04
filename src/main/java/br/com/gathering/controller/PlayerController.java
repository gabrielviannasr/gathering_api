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

import br.com.gathering.dto.PlayerDTO;
import br.com.gathering.entity.Player;
import br.com.gathering.service.PlayerService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/player")
public class PlayerController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/player";
	private static final String ENTITY = "Player";

	@Autowired
	private PlayerService service;

	@GetMapping
	public List<Player> getList(Player model) {
		LogHelper.info(log, RouteHelper.GET(PATH), ENTITY, model);
		return service.getList(model);
	}

	@GetMapping("/page")
	public Page<Player> getPage(Player model,
			@SortDefault.SortDefaults({ @SortDefault(sort = "name"), @SortDefault(sort = "username") }) Sort sort,
			@RequestParam int page,
			@RequestParam int size) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/page"), "page", page, "size", size);
		return service.getPage(model, sort, page, size);
	}

	@GetMapping("/{id}")
	public Player getById(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/{id}"), "id", id);
		return service.getById(id);
	}

	@PostMapping
	public Player save(@RequestBody PlayerDTO dto) {
		Player model = dto.toModel();
		LogHelper.info(log, RouteHelper.POST(PATH), "payload", model);
		return service.save(model);
	}

	@PutMapping("/{id}")
	public Player update(@PathVariable Long id, @RequestBody PlayerDTO dto) {
		Player model = dto.toModel();
		model.setId(id);
		LogHelper.info(log, RouteHelper.PUT(PATH), "id", id, "payload", model);
		return service.save(model);
	}

}
