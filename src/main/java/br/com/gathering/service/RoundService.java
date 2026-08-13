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

import br.com.gathering.dto.request.RoundDTO;
import br.com.gathering.entity.Event;
import br.com.gathering.entity.EventFee;
import br.com.gathering.entity.Player;
import br.com.gathering.entity.Round;
import br.com.gathering.repository.EventRepository;
import br.com.gathering.repository.PlayerRepository;
import br.com.gathering.repository.RoundRepository;
import br.com.gathering.util.LogHelper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class RoundService extends AbstractService<Round> {

	private static final Logger log = LogHelper.getLogger();
	private static final String ENTITY = "Round";

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

	public List<Round> getList(Long idEvent, Round model) {

		model.setIdEvent(idEvent);
		
		LogHelper.info(log, "Fetching list", "model", model);

		List<Round> result = repository.findAll(getExample(model), getSort());

        LogHelper.info(log, "Fetched list", "count", result.size());

        return result;
	}

	public Page<Round> getPage(Long idEvent, Round model, Sort sort, int page, int size) {

		model.setIdEvent(idEvent);

		LogHelper.info(log, "Fetching paged list", "page", page, "size", size);

        Page<Round> result = repository.findAll(getExample(model), PageRequest.of(page, size, sort));

        LogHelper.info(log, "Fetched paged list", "totalElements", result.getTotalElements());

        return result;
	}

	public Round getById(Long id) {

	    LogHelper.info(log, "Fetching by id", "id", id);

	    Round found = repository.findById(id)
	            .orElseThrow(() -> {
	                LogHelper.warn(log, ENTITY + " not found", "id", id);
	                return new ResponseStatusException(
	                        HttpStatus.NOT_FOUND, ENTITY + " not found");
	            });

	    LogHelper.info(log, "Found", "id", found.getId());

	    return found;
	}

	public Round getByIdEventAndRound(Long idEvent, Integer round) {

		LogHelper.info(log, "Fetching by idEvent and round", "idEvent", idEvent, "round", round);

		Round found = repository.findByIdEventAndRound(idEvent, round)
	            .orElseThrow(() -> {
	                LogHelper.warn(log, ENTITY + " not found", "idEvent", idEvent, "round", round);
	                return new ResponseStatusException(
	                        HttpStatus.NOT_FOUND, ENTITY + " not found");
	            });

	    LogHelper.info(log, "Found", "id", found.getId());

	    return found;
	}

	public int getNextRound(Long idEvent) {

	    LogHelper.info(log, "Fetching next round", "idEvent", idEvent);
	    
	    eventService.getById(idEvent);

	    int round = repository.getNextRound(idEvent);

	    LogHelper.info(log, "Next round", "round", round);

	    return round;
	}

	@Transactional
	public Round create(Long idEvent, RoundDTO dto) {

		Event event = eventService.getById(idEvent);

		eventService.validateEditable(event);

		Round model = dto.toModel();

		model.setIdEvent(idEvent);

		model.init();

		validate(model);

		LogHelper.info(log, "Saving", "model", model);

		Round saved = repository.save(model);

		eventService.refresh(idEvent);

		LogHelper.info(log, "Saved", "id", saved.getId());

		return saved;
	}

	@Transactional
	public Round update(Long idEvent, Integer round, RoundDTO dto) {

		Event event = eventService.getById(idEvent);

		eventService.validateEditable(event);

		Round current = getByIdEventAndRound(idEvent, round);

		Round model = dto.toModel();

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

	    LogHelper.info(log, "Updating", "model", current);

	    Round saved = repository.save(current);

	    eventService.refresh(saved.getIdEvent());

	    LogHelper.info(log, "Updated", "id", saved.getId());

	    return saved;
	}
	
	private void calculate(Round model) {

		model.setPlayersTotal(model.getPlayers().size());
		
		Event event = eventRepository.findById(model.getIdEvent())
			    .orElseThrow(() -> new EntityNotFoundException("Event not found"));
		
		EventFee fee = event.getFees()
			    .stream()
			    .filter(f -> f.getId().getPlayers().equals(model.getPlayersTotal()))
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

	private void validate(Round model) {

	    if (model.getIdPlayerWinner() != null &&
	        model.getPlayers().stream().noneMatch(p -> p.getId().equals(model.getIdPlayerWinner()))) {

//	        throw new BusinessException("Winner must be one of the round players.");
	    	throw new ResponseStatusException(
	    		    HttpStatus.BAD_REQUEST,
	    		    "Winner must be one of the round players.");
	    }

	}

}
