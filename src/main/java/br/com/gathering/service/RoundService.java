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

import br.com.gathering.entity.Event;
import br.com.gathering.entity.EventFee;
import br.com.gathering.entity.Player;
import br.com.gathering.entity.Round;
import br.com.gathering.repository.EventRepository;
import br.com.gathering.repository.PlayerRepository;
import br.com.gathering.repository.RoundRepository;
import br.com.gathering.util.LogHelper;
import jakarta.persistence.EntityNotFoundException;

@Service
public class RoundService extends AbstractService<Round> {

	private static final Logger log = LogHelper.getLogger();

	@Autowired
	private RoundRepository repository;
	
	@Autowired
	private PlayerRepository playerRepository;
	
	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private EventService eventService;

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

	public Round getByRound(Round model) {
		LogHelper.info(log, "Fetching by Round", "round", model.getRound(), "idEvent", model.getIdEvent());

		Optional<Round> optional = repository.findOne(getExample(model));
		
		if (optional.isEmpty()) {
			LogHelper.warn(log, "Not found", "round", model.getRound(), "idEvent", model.getIdEvent());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Round round = optional.get();
		LogHelper.info(log, "Found", "id", round.getId());
		return round;
	}

	public Round create(Round model) {
		model.init();
		// validate(model);
		LogHelper.info(log, "Saving", "payload", model);

		Round saved = repository.save(model);

		eventService.refresh(saved.getIdEvent());

		LogHelper.info(log, "Saved", "id", saved.getId());
		return saved;
	}
	
	public Round update(Round model) {

		Round current = repository
			    .findByIdEventAndRound(model.getIdEvent(), model.getRound())
			    .orElseThrow(() ->
			        new EntityNotFoundException("Round not found"));

	    current.setPlayersTotal(model.getPlayersTotal());
	    current.setIdPlayerWinner(model.getIdPlayerWinner());
	    current.setCanceled(model.getCanceled());

	    // Load managed Player entities from the database before updating the relationship.
	    // Using detached instances created from the DTO may prevent Hibernate from
	    // synchronizing the join table correctly.
//	    current.setPlayers(model.getPlayers());	  
	    List<Player> players = playerRepository.findAllById(
	            model.getPlayers()
	                    .stream()
	                    .map(Player::getId)
	                    .toList());

	    // Keep the managed collection instance and update only its contents.
	    // Replacing the collection may break Hibernate's change tracking for @ManyToMany.
//    	current.setPlayers(players);
	    current.getPlayers().clear();
	    current.getPlayers().addAll(players);

	    calculate(current);
	    validate(current);

	    LogHelper.info(log, "Updating", "payload", current);

	    Round saved = repository.save(current);

	    eventService.refresh(saved.getIdEvent());

	    LogHelper.info(log, "Updated", "id", saved.getId());

	    return saved;
	}
	
	private void validate(Round model) {

	    if (model.getIdPlayerWinner() != null &&
	        model.getPlayers().stream().noneMatch(p -> p.getId().equals(model.getIdPlayerWinner()))) {

//	        throw new BusinessException("Winner must be one of the round players.");
	    	throw new ResponseStatusException(
	    		    HttpStatus.BAD_REQUEST,
	    		    "Winner must be one of the round players.");
	    }

	}
	
	private void calculate(Round model) {
		model.setPlayersTotal(model.getPlayers().size());
		
		Event event = eventRepository.findById(model.getIdEvent())
			    .orElseThrow(() -> new EntityNotFoundException("Event not found"));
		
		EventFee fee = event.getFees()
			    .stream()
			    .filter(f -> f.getPlayers().equals(model.getPlayersTotal()))
			    .findFirst()
			    .orElse(null);
		
		if (fee != null) {
		    model.setPrize(fee.getPrizeFee());
		    model.setLoserPot(fee.getLoserFee());
		} else {
		    model.setPrize(event.getRoundFee() * model.getPlayersTotal());
		    model.setLoserPot(0.0);
		}
	}

}
