package br.com.gathering.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.gathering.calculation.ResultCalculator;
import br.com.gathering.entity.Event;
import br.com.gathering.entity.Result;
import br.com.gathering.entity.Transaction;
import br.com.gathering.factory.TransactionFactory;
import br.com.gathering.projection.RankProjection;
import br.com.gathering.projection.event.ConfraPotProjection;
import br.com.gathering.projection.event.EventSummaryProjection;
import br.com.gathering.projection.event.LoserPotProjection;
import br.com.gathering.projection.event.RankCountProjection;
import br.com.gathering.repository.EventRepository;
import br.com.gathering.repository.ResultRepository;
import br.com.gathering.repository.TransactionRepository;
import br.com.gathering.util.LogHelper;

@Service
public class ResultService extends AbstractService<Result> {

	private static final Logger log = LoggerFactory.getLogger(ResultService.class);

	@Autowired
	private ResultRepository repository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	public ConfraPotProjection getConfraPot(Long idEvent) {
		ConfraPotProjection confraPot = repository.getConfraPot(idEvent);

		// Log
		System.out.printf(
			"ConfraPot: { players: %-2d | confraPot: %.2f }%n",
			confraPot.getPlayers(),
			confraPot.getConfraPot()
		);

		return confraPot;
	}

	public LoserPotProjection getLoserPot(Long idEvent) {
		LoserPotProjection loserPot = repository.getLoserPot(idEvent);

		// Log
		System.out.printf(
			"LoserPot: { rounds: %-2d | loserPot: %.2f }%n",
			loserPot.getRounds(),
			loserPot.getLoserPot()
		);

		return loserPot;
	}

	public List<RankCountProjection> getRankCount(Long idEvent) {
		List<RankCountProjection> list = repository.getRankCount(idEvent);

		// Log
		list.forEach(item -> 
			System.out.printf(
	            "\t{ rank: %-2d | count: %-2d }%n",
	            item.getRank(),
	            item.getCount()
	        )
		);

		return list;
	}

	public List<RankProjection> getRankProjection(Long idEvent) {
		List<RankProjection> list = repository.getRankProjection(idEvent);

		int maxNameLength = list.stream()
		    .map(RankProjection::getPlayerName)
		    .filter(Objects::nonNull)
		    .mapToInt(String::length)
		    .max()
		    .orElse(25); // fallback

		// String format = "\t{ rank: %-2d | name: %-" + Player.NAME_LENGTH + "s | rankBalance: %8.2f }%n";
		String format = "\t{ rank: %-2d | name: %-" + maxNameLength + "s | rankBalance: %8.2f }%n";

		// Log
		list.forEach(item -> 
	        System.out.printf(
	            format,
	            item.getRank(),
	            item.getPlayerName(),
	            item.getRankBalance()
	        )
		);

		return list;
	}

	public Result getResult(Long idEvent, Long idPlayer) {

	    LogHelper.info(log, "Fetching result", "idEvent", idEvent, "idPlayer", idPlayer);

	    Optional<Result> optional = repository.findByIdEventAndIdPlayer(idEvent, idPlayer);

	    if (optional.isEmpty()) {
	        LogHelper.warn(log, "Result not found", "idEvent", idEvent, "idPlayer", idPlayer);

	        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	    }

	    Result result = optional.get();

	    LogHelper.info(log, "Result found", "idEvent", idEvent, "idPlayer", idPlayer);

	    return result;
	}
	
	@Transactional
	public List<Result> getResult(Long idEvent) {
	    Event event = eventRepository.findById(idEvent)
	            .orElseThrow(() -> new IllegalArgumentException("Event not found: " + idEvent));

	    if (needsRefresh(event)) {
	        refreshResult(event);
	    }

	    return repository.findByIdEvent(idEvent);
	}
	
	private boolean needsRefresh(Event event) {
	    return event.getResultsAt() == null
	        || event.getUpdatedAt().isAfter(event.getResultsAt());
	}

	public void refreshResult(Event event) {

		Long idEvent = event.getId();

		List<RankProjection> ranks = repository.getRankProjection(idEvent);
		LoserPotProjection loserPot = repository.getLoserPot(idEvent);
		List<RankCountProjection> rankCount = repository.getRankCount(idEvent);

		List<Result> results = ResultCalculator.calculate(
		    idEvent,
		    ranks,
		    loserPot,
		    rankCount
		);

		if (results.isEmpty()) {
		    return;
		}

		deleteSnapshot(idEvent);

		saveResultSnapshot(event, results);
	}

	private void deleteSnapshot(Long idEvent) {

	    transactionRepository.deleteByIdEvent(idEvent);

	    repository.deleteByIdEvent(idEvent);

	    transactionRepository.flush();

	    repository.flush();
	}

	private void saveResultSnapshot(Event event, List<Result> results) {

	    repository.saveAll(results);

	    saveTransactions(event, results);

	    event.setResultsAt(LocalDateTime.now());

	    eventRepository.save(event);
	}

	private void saveTransactions(Event event, List<Result> results) {

	    List<Transaction> transactions = TransactionFactory.fromResults(event, results);

	    transactionRepository.saveAll(transactions);
	}

	public EventSummaryProjection getSummaryProjection(Long idEvent) {
		return repository.getSummaryProjection(idEvent);
	}

}
