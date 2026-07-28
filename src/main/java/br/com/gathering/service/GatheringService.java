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

import br.com.gathering.entity.Gathering;
import br.com.gathering.repository.GatheringRepository;
import br.com.gathering.util.LogHelper;

@Service
public class GatheringService extends AbstractService<Gathering> {

	private static final Logger log = LogHelper.getLogger();

	@Autowired
	private GatheringRepository repository;

	public static Sort getSort() {
		return Sort.by(Order.asc("name"));
	}

	public List<Gathering> getList(Gathering model) {
		List<Gathering> result = repository.findAll(getExample(model), getSort());
        LogHelper.info(log, "Fetched list", "count", result.size());
        return result;
	}

	public Page<Gathering> getPage(Gathering model, Sort sort, int page, int size) {
		LogHelper.info(log, "Fetching paged list", "page", page, "size", size);
        Page<Gathering> result = repository.findAll(getExample(model), PageRequest.of(page, size, sort));
        LogHelper.info(log, "Fetched paged list", "totalElements", result.getTotalElements());
        return result;
	}

	public Gathering getById(Long id) {
	    LogHelper.info(log, "Fetching by ID", "id", id);

	    Gathering gathering = repository.findById(id)
	            .orElseThrow(() -> {
	                LogHelper.warn(log, "Gathering not found", "id", id);
	                return new ResponseStatusException(
	                        HttpStatus.NOT_FOUND,
	                        "Gathering not found");
	            });

	    LogHelper.info(log, "Found", "id", gathering.getId());

	    return gathering;
	}

	public Gathering save(Gathering model) {
		model.init();
//		validate(model);
		LogHelper.info(log, "Saving", "payload", model);
		Gathering saved = repository.save(model);
        LogHelper.info(log, "Saved", "id", saved.getId());
        return saved;
	}

	public Gathering update(Gathering gathering) {
	    Gathering current = getById(gathering.getId());

	    current.setName(gathering.getName());

	    return repository.save(current);
	}

	public List<Integer> getYears() {
	    List<Integer> years = repository.findAvailableYears();
	    LogHelper.info(log, "Fetched years", "count", years.size());
	    return years;
	}

}
