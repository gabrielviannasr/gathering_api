package br.com.gathering.service;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.gathering.dto.request.PlayerDTO;
import br.com.gathering.entity.Player;
import br.com.gathering.repository.PlayerRepository;
import br.com.gathering.util.LogHelper;
import jakarta.transaction.Transactional;

@Service
public class PlayerService extends AbstractService<Player> {

	private static final Logger log = LogHelper.getLogger();
	private static final String ENTITY = "Player";

	@Autowired
	private PlayerRepository repository;

	public static Sort getSort() {
		return Sort.by(Order.asc("name"));
	}

	public List<Player> getList(Player model) {

		LogHelper.info(log, "Fetching list", "model", model);

		List<Player> result = repository.findAll(getExample(model), getSort());

        LogHelper.info(log, "Fetched list", "count", result.size());

        return result;
	}

	public Page<Player> getPage(Player model, Sort sort, int page, int size) {

		LogHelper.info(log, "Fetching paged list", "page", page, "size", size);

        Page<Player> result = repository.findAll(getExample(model), PageRequest.of(page, size, sort));

        LogHelper.info(log, "Fetched paged list", "totalElements", result.getTotalElements());

        return result;
	}

	public Player getById(Long id) {

	    LogHelper.info(log, "Fetching by id", "id", id);

	    Player found = repository.findById(id)
	            .orElseThrow(() -> {
	                LogHelper.warn(log, ENTITY + " not found", "id", id);
	                return new ResponseStatusException(
	                        HttpStatus.NOT_FOUND, ENTITY + " not found");
	            });

	    LogHelper.info(log, "Found", "id", found.getId());

	    return found;
	}

	public Player create(PlayerDTO dto) {

		Player model = dto.toModel();

	    model.init();

	    validate(model);

	    LogHelper.info(log, "Saving", "model", model);

	    Player saved = repository.save(model);

	    LogHelper.info(log, "Saved", "id", saved.getId());

	    return saved;
	}

	@Transactional
	public Player update(Long id, PlayerDTO dto) {

		Player current = getById(id);

		Player model = dto.toModel();

	    current.setName(model.getName());

	    validate(current);

	    LogHelper.info(log, "Updating", "model", current);

	    Player updated = repository.save(current);

	    LogHelper.info(log, "Updated", "id", updated.getId());

	    return updated;
	}

	private void validate(Player model) {
		
	}

}
