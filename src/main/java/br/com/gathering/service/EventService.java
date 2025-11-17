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
import br.com.gathering.entity.Round;
import br.com.gathering.repository.EventRepository;
import br.com.gathering.repository.RoundRepository;
import br.com.gathering.util.LogHelper;
import jakarta.transaction.Transactional;

@Service
public class EventService extends AbstractService<Event> {

	private static final Logger log = LogHelper.getLogger();

	@Autowired
	private EventRepository repository;

	@Autowired
	private RoundRepository roundRepository;

	public static Sort getSort() {
		return Sort.by(Order.asc("idGathering"), Order.asc("createdAt"));
	}

	public List<Event> getList(Event model) {
//		LogHelper.info(log, "Fetching list", "filter", model);
        List<Event> result = repository.findAll(getExample(model), getSort());
        LogHelper.info(log, "Fetched list", "count", result.size());
        return result;
	}

	public Page<Event> getPage(Event model, Sort sort, int page, int size) {
		LogHelper.info(log, "Fetching paged list", "page", page, "size", size);
        Page<Event> result = repository.findAll(getExample(model), PageRequest.of(page, size, sort));
        LogHelper.info(log, "Fetched paged list", "totalElements", result.getTotalElements());
        return result;
	}

	public Event getById(Long id) {
//		Optional<Event> optional = repository.findById(id);
//		return optional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		LogHelper.info(log, "Fetching by ID", "id", id);
        Optional<Event> optional = repository.findById(id);
        if (optional.isEmpty()) {
            LogHelper.warn(log, "Not found", "id", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Event event = optional.get();
        LogHelper.info(log, "Found", "id", event.getId());
        return event;
	}

	@Transactional
	public Event save(Event model) {
	    model.init();
	    validate(model);

	    LogHelper.info(log, "Saving", "payload", model);
	    Event saved = repository.save(model);
	    LogHelper.info(log, "Saved", "id", saved.getId());

	    updateRoundsBasedOnFees(saved);

	    return saved;
	}

	private void validate(Event model) {
	    if (model.getFees() == null || model.getFees().isEmpty()) return;

	    for (EventFee fee : model.getFees()) {
	        double totalArrecadado = model.getRoundFee() * fee.getPlayers();
	        double totalDistribuido = fee.getPrizeFee() + fee.getLoserFee();

	        if (Math.abs(totalArrecadado - totalDistribuido) > 0.001) {
	        	LogHelper.warn(log, "Invalid fee configuration", "roundFee", model.getRoundFee(), "players", fee.getPlayers(), "loserFee", fee.getLoserFee(), "prizeFee", fee.getPrizeFee());
	            throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                String.format(
	                    "Distribuição inválida para %d jogadores: arrecadado = %.2f, distribuído = %.2f (diferença = %.2f)",
	                    fee.getPlayers(), totalArrecadado, totalDistribuido, totalArrecadado - totalDistribuido
	                )
	            );
	        }
	        LogHelper.info(log, "Valid fee configuration", "roundFee", model.getRoundFee(), "players", fee.getPlayers(), "loserFee", fee.getLoserFee(), "prizeFee", fee.getPrizeFee());
	    }
	}

	private void updateRoundsBasedOnFees(Event event) {

	    LogHelper.info(log, "Updating rounds after fee changes", "eventId", event.getId());

	    List<Round> rounds = roundRepository.findByIdEvent(event.getId());

	    for (Round round : rounds) {

	        int players = round.getPlayers();

	        EventFee fee = event.getFees()
	            .stream()
	            .filter(f -> f.getPlayers() == players)
	            .findFirst()
	            .orElse(null);

	        double oldPrize = round.getPrize();
	        double oldLoser = round.getLoserPot();

	        if (fee == null) {

	            // REMOVIDO → aplicar regra padrão
	            double newPrize = players * event.getRoundFee();
	            double newLoser = 0;

	            boolean changed = (oldPrize != newPrize) || (oldLoser != newLoser);

	            if (changed) {
	                LogHelper.info(log,
	                    "Removing fee config",
	                    "roundId", round.getId(),
	                    "newPrize", String.format("%.2f", newPrize),
	                    "newLoserPot", String.format("%.2f", newLoser)
	                );

	                round.setPrize(newPrize);
	                round.setLoserPot(newLoser);
	                roundRepository.save(round);
	            }

	            continue;
	        }

	        // Caso exista configuração
	        double newPrize = fee.getPrizeFee();
	        double newLoser = fee.getLoserFee();

	        boolean changed = false;

	        if (oldPrize != newPrize) {
	            LogHelper.info(log,
	                "Updating prizePot",
	                "roundId", round.getId(),
	                "old", String.format("%.2f", oldPrize),
	                "new", String.format("%.2f", newPrize)
	            );
	            round.setPrize(newPrize);
	            changed = true;
	        }

	        if (oldLoser != newLoser) {
	            LogHelper.info(log,
	                "Updating loserPot",
	                "roundId", round.getId(),
	                "old", String.format("%.2f", oldLoser),
	                "new", String.format("%.2f", newLoser)
	            );
	            round.setLoserPot(newLoser);
	            changed = true;
	        }

	        if (changed) {
	            roundRepository.save(round);
	        }
	    }
	}

}
