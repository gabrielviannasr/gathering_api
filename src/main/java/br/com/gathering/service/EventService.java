package br.com.gathering.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.gathering.dto.request.EventDTO;
import br.com.gathering.entity.Event;
import br.com.gathering.entity.EventFee;
import br.com.gathering.entity.Result;
import br.com.gathering.entity.Round;
import br.com.gathering.entity.Transaction;
import br.com.gathering.factory.TransactionFactory;
import br.com.gathering.projection.EventRefreshProjection;
import br.com.gathering.repository.EventFeeRepository;
import br.com.gathering.repository.EventRepository;
import br.com.gathering.repository.ResultRepository;
import br.com.gathering.repository.RoundRepository;
import br.com.gathering.repository.TransactionRepository;
import br.com.gathering.util.LogHelper;
import jakarta.transaction.Transactional;

@Service
public class EventService extends AbstractService<Event> {

	private static final Logger log = LogHelper.getLogger();
	private static final String ENTITY = "Event";

	@Autowired
	private EventRepository repository;

	@Autowired
	private EventFeeRepository eventFeeRepository;

	@Autowired
	private ResultRepository resultRepository;
	
	@Autowired
	private RoundRepository roundRepository;

	@Autowired
	private ResultService resultService;

	@Autowired
	private TransactionRepository transactionRepository;

	public static Sort getSort() {
		return Sort.by(Order.asc("idGathering"), Order.asc("createdAt"));
	}

	public List<Event> getList(Event model) {

		LogHelper.info(log, "Fetching list", "model", model);

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

	    LogHelper.info(log, "Fetching by id", "id", id);

	    Event found = repository.findById(id)
	            .orElseThrow(() -> {
	                LogHelper.warn(log, ENTITY + " not found", "id", id);
	                return new ResponseStatusException(
	                        HttpStatus.NOT_FOUND, ENTITY + " not found");
	            });

	    LogHelper.info(log, "Found", "id", found.getId());

	    return found;
	}

	@Transactional
	public Event create(EventDTO dto) {

		Event model = dto.toModel();

	    model.init();

	    validate(model);

	    LogHelper.info(log, "Saving", "model", model);

	    Event saved = repository.save(model);

	    LogHelper.info(log, "Saved", "id", saved.getId());

	    return saved;
	}

	@Transactional
	public Event update(Long id, EventDTO dto) {

		Event current = getById(id);

	    validateEditable(current);

	    Event model = dto.toModel();

	    current.setIdFormat(model.getIdFormat());
	    current.setConfraFee(model.getConfraFee() == null ? 0.0 : model.getConfraFee());
	    current.setRoundFee(model.getRoundFee() == null ? 0.0 : model.getRoundFee());
	    current.setUpdatedAt(LocalDateTime.now());

//	    eventFeeRepository.deleteByIdEvent(id);
//	    eventFeeRepository.flush();

//	    current.getFees().clear();

//	    for (EventFee fee : model.getFees()) {
//	    	fee.setIdEvent(id);
//	        fee.setEvent(current);
//	        current.getFees().add(fee);
//	    }
	    
	    Set<Integer> incomingPlayers = model.getFees().stream()
	    	    .map(EventFee::getPlayers)
	    	    .collect(Collectors.toSet());

	    	current.getFees().removeIf(
	    	    fee -> !incomingPlayers.contains(fee.getPlayers())
	    	);


	    for (EventFee incoming : model.getFees()) {

	        EventFee existing = current.getFees().stream()
	            .filter(fee -> fee.getPlayers().equals(incoming.getPlayers()))
	            .findFirst()
	            .orElse(null);

	        if (existing != null) {
	            existing.setPrizeFee(incoming.getPrizeFee());
	            existing.setLoserFee(incoming.getLoserFee());
	        } else {
	            incoming.setEvent(current);
	            current.getFees().add(incoming);
	        }
	    }

	    validate(current);

	    LogHelper.info(log, "Updating", "model", current);
	    
	    Event updated = repository.save(current);

	    refreshRounds(id);
	    
	    refresh(id);
	    
	    LogHelper.info(log, "Updated", "id", updated.getId());

	    return updated;
	}

	@Transactional
	public Event cancel(Long id) {

		LogHelper.info(log, "Canceling", "id", id);

	    Event event = getById(id);

	    validateCancel(event);

	    deleteTransactions(event);

	    resultService.deleteSnapshot(id);

	    roundRepository.cancelByIdEvent(id);

	    refresh(id);

	    event = getById(id);

	    // Um evento cancelado não pode permanecer finalizado.
	    event.setCanceled(true);
	    event.setFinalized(false);
	    event.setUpdatedAt(LocalDateTime.now());

	    Event saved = repository.save(event);

	    LogHelper.info(log, "Canceled", "id", saved.getId());

	    return saved;
	}

	@Transactional
	public Event finalize(Long id) {

		LogHelper.info(log, "Finalizing", "id", id);

	    refresh(id);

	    Event event = getById(id);

	    validateFinalize(event);

	    resultService.refreshResult(event);

	    deleteTransactions(event);

	    createTransactions(event);

	    LocalDateTime now = LocalDateTime.now();
	    event.setFinalized(true);
	    event.setUpdatedAt(now);
	    event.setResultsAt(now);

	    Event saved = repository.save(event);

	    LogHelper.info(log, "Finalized", "id", saved.getId());

	    return saved;
	}

	@Transactional
	public Event reactivate(Long id) {

		LogHelper.info(log, "Reactivating", "id", id);

	    Event event = getById(id);

	    validateReactivate(event);

	    event.setCanceled(false);
	    event.setUpdatedAt(LocalDateTime.now());

	    Event saved = repository.save(event);

	    LogHelper.info(log, "Reactivated", "id", saved.getId());

	    return saved;
	}

	@Transactional
	public Event reopen(Long id) {

		LogHelper.info(log, "Reopening", "id", id);

	    Event event = getById(id);

	    validateReopen(event);

	    deleteTransactions(event);

	    event.setFinalized(false);
	    event.setUpdatedAt(LocalDateTime.now());

	    Event saved = repository.save(event);

	    LogHelper.info(log, "Reopened", "id", saved.getId());

	    return saved;
	}

	private void createTransactions(Event event) {

	    List<Result> results = resultRepository.findByIdEvent(event.getId());

	    List<Transaction> transactions = TransactionFactory.fromResults(event, results);

	    transactionRepository.saveAll(transactions);
	}

	private void deleteTransactions(Event event) {

	    transactionRepository.deleteByIdEvent(event.getId());

	    transactionRepository.flush();
	}
	
	public void markAsUpdated(Long idEvent) {

	    Event event = getById(idEvent);

	    event.setUpdatedAt(LocalDateTime.now());

	    repository.save(event);
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
	    event.setUpdatedAt(LocalDateTime.now());

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

	public void validateCancel(Event event) {

	    if (event.getCanceled()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Evento já está cancelado."
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

	public void validateReactivate(Event event) {

	    if (!event.getCanceled()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Evento já está ativo."
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

}
