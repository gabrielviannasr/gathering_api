package br.com.gathering.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.gathering.entity.Round;
import br.com.gathering.repository.RoundRepository;
import br.com.gathering.util.LogHelper;

@Service
public class RoundService extends AbstractService<Round> {

	private static final Logger log = LogHelper.getLogger();

	@Autowired
	private RoundRepository repository;

	public static Sort getSort() {
		return Sort.by(Order.asc("createdAt"));
	}

	public List<Round> getList(Round model) {
		List<Round> result = repository.findAll(getExample(model), getSort());
		LogHelper.info(log, "Fetched list", "count", result.size());
		return result;
	}

	public Page<Round> getPage(Round model, Sort sort, int page, int size) {
		LogHelper.info(log, "Fetching paged list", "page", page, "size", size);
        Page<Round> result = repository.findAll(getExample(model), PageRequest.of(page, size, sort));
        LogHelper.info(log, "Fetched paged list", "totalElements", result.getTotalElements());
        return result;
	}

	public Round getById(Long id) {
		LogHelper.info(log, "Fetching by ID", "id", id);
		Optional<Round> optional = repository.findById(id);
		if (optional.isEmpty()) {
			LogHelper.warn(log, "Not found", "id", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Round round = optional.get();
		LogHelper.info(log, "Found", "id", round.getId());
		return round;
	}

	public Round save(Round model) {
		model.init();
		// validate(model);
		LogHelper.info(log, "Saving", "payload", model);
		Round saved = repository.save(model);
		LogHelper.info(log, "Saved", "id", saved.getId());
		return saved;
	}

}
