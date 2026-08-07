package br.com.gathering.service;

import java.time.LocalDateTime;
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
import br.com.gathering.projection.EventRefreshProjection;
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
	public Event create(Event model) {
	    model.init();

	    validate(model);

	    Event saved = repository.save(model);

	    return saved;
	}

	@Transactional
	public Event update(Long id, Event model) {

	    Event saved = getById(id);

	    validateEditable(saved);

	    model.setId(id);
	    model.setCanceled(saved.getCanceled());
	    model.setFinalized(saved.getFinalized());
	    model.setCreatedAt(saved.getCreatedAt());
	    model.setUpdatedAt(saved.getUpdatedAt());
	    model.setResultsAt(saved.getResultsAt());
	    model.setPlayers(saved.getPlayers());
	    model.setRounds(saved.getRounds());
	    model.setLoserPot(saved.getLoserPot());
	    model.setConfraPot(saved.getConfraPot());
	    model.setPrize(saved.getPrize());
	    

	    model.init();

	    validate(model);

	    Event updated = repository.save(model);

	    refreshRounds(updated.getId());

	    return updated;
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

	@Transactional
	public void refresh(Long idEvent) {

	    Event event = getById(idEvent);

	    EventRefreshProjection stats = repository.getRefreshProjection(idEvent);

	    event.setPlayers(stats.getPlayers());
	    event.setRounds(stats.getRounds());
	    event.setLoserPot(stats.getLoserPot());
	    event.setConfraPot(stats.getConfraPot());
	    event.setPrize(stats.getPrize());

//	    event.setUpdatedAt(LocalDateTime.now());

	    repository.save(event);
	}

	private void refreshRounds(Long idEvent) {
		
		Event event = getById(idEvent);

	    LogHelper.info(log, "Updating rounds after fee changes", "eventId", event.getId());

	    List<Round> rounds = roundRepository.findByIdEvent(event.getId());

	    for (Round round : rounds) {

	        int playersTotal = round.getPlayersTotal();

	        EventFee fee = event.getFees()
	            .stream()
	            .filter(f -> f.getPlayers() == playersTotal)
	            .findFirst()
	            .orElse(null);

	        double oldPrize = round.getPrize();
	        double oldLoser = round.getLoserPot();

	        if (fee == null) {

	            // REMOVIDO → aplicar regra padrão
	            double newPrize = playersTotal * event.getRoundFee();
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

	public void markAsUpdated(Long idEvent) {
	    Event event = getById(idEvent);
	    event.setUpdatedAt(LocalDateTime.now());
	    repository.save(event);
	}
	
	@Transactional
	public Event finalize(Long id) {

	    refresh(id);

	    Event event = getById(id);

	    validateFinalize(event);

	    // TODO deleteTransactions(id);
	    // TODO createTransactions(id);

	    LocalDateTime now = LocalDateTime.now();
	    event.setFinalized(true);
	    event.setUpdatedAt(now);
	    event.setResultsAt(now);

	    return repository.save(event);
	}

	@Transactional
	public Event reopen(Long id) {

	    Event event = getById(id);

	    validateReopen(event);

	    // TODO deleteTransactions(id);

	    event.setFinalized(false);
	    event.setUpdatedAt(LocalDateTime.now());

	    return repository.save(event);
	}

	private void validateFinalize(Event event) {

	    if (event.getCanceled()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Eventos cancelados não podem ser finalizados."
	        );
	    }

	    if (event.getFinalized()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Evento já está finalizado."
	        );
	    }

	    List<Round> rounds = roundRepository.findByIdEventAndCanceledFalse(event.getId());

	    if (rounds.isEmpty()) {
	    	throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "O evento deve possuir pelo menos uma rodada ativa."
	        );
	    }

	    rounds.forEach(this::validateFinalizeRound);

	}

	private void validateFinalizeRound(Round round) {

	    if (round.getPlayersTotal() < 2) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            String.format(
	                "Rodada %d deve possuir pelo menos dois jogadores.",
	                round.getRound()
	            )
	        );
	    }

	    if (round.getIdPlayerWinner() == null) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            String.format(
	                "Rodada %d deve possuir um vencedor.",
	                round.getRound()
	            )
	        );
	    }
	}

	private void validateReopen(Event event) {

	    if (event.getCanceled()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Eventos cancelados não podem ser reabertos."
	        );
	    }

	    if (!event.getFinalized()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Evento já está aberto."
	        );
	    }
	}

	public void validateEditable(Event event) {

	    if (event.getCanceled()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Eventos cancelados não podem ser alterados."
	        );
	    }

	    if (event.getFinalized()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Eventos finalizados não podem ser alterados."
	        );
	    }
	}

	@Transactional
	public Event cancel(Long id) {

	    Event event = getById(id);

	    validateCancel(event);

	    // TODO deleteTransactions(id);

	    roundRepository.cancelByIdEvent(id);

	    refresh(id);

	    event = getById(id);

	    // Um evento cancelado não pode permanecer finalizado.
	    event.setCanceled(true);
	    event.setFinalized(false);
	    event.setUpdatedAt(LocalDateTime.now());

	    return repository.save(event);
	}

	@Transactional
	public Event reactivate(Long id) {

	    Event event = getById(id);

	    validateReactivate(event);

	    event.setCanceled(false);
	    event.setUpdatedAt(LocalDateTime.now());

	    return repository.save(event);
	}

	public void validateCancel(Event event) {

	    if (event.getCanceled()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Evento já está cancelado."
	        );
	    }
	}

	public void validateReactivate(Event event) {

	    if (!event.getCanceled()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Evento já está ativo."
	        );
	    }
	}
}
