package br.com.gathering.calculation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.gathering.entity.Result;
import br.com.gathering.projection.RankProjection;
import br.com.gathering.projection.event.LoserPotProjection;
import br.com.gathering.projection.event.RankCountProjection;

public final class ResultCalculator {

	// The worst-ranked player takes the largest piece of loserPot
	private static final double WORST_RANK_LOSER_POT_PERCENTAGE = 0.6;
	// The second worst-ranked players share the smallest piece of loserPot
	private static final double SECOND_WORST_RANK_LOSER_POT_PERCENTAGE = 0.4;

	private static List<Result> buildResults(Long idEvent, List<RankProjection> ranks) {
	    return ranks.stream()
	        .map(p -> Result.builder()
	            .idEvent(idEvent)
	            .idPlayer(p.getIdPlayer())
	            .playerName(p.getPlayerName())
	            .rank(p.getRank())
	            .wins(p.getWins())
	            .rounds(p.getRounds())
	            .positive(p.getPositive())
	            .negative(p.getNegative())
	            .rankBalance(p.getRankBalance())
	            .loserPot(0.0)
	            .finalBalance(p.getRankBalance())
	            .build())
	        .collect(Collectors.toList());
	}

    private static void distributeLoserPot(
            Long idEvent,
            List<Result> results,
            LoserPotProjection loserPot,
            List<RankCountProjection> rankCount) {

    	if (rankCount.get(0).getCount() > 1) {
			distributeLoserPotEqually(idEvent, results, loserPot.getLoserPot(), rankCount);
		} else {
			distributeLoserPotUnequally(idEvent, results, loserPot.getLoserPot(), rankCount);
		}
    }

    private static void distributeLoserPotEqually(Long idEvent, List<Result> results, Double loserPot, List<RankCountProjection> rankCount) {
		// LoserPot equally divided among the worst-ranked players
		Double percentage = 1.0 / rankCount.get(0).getCount();

		// Loop to update loserPot and finalBalance
		results.forEach(item -> {
			// Non-worst-ranked players take 0% of loserPot
			Double pot = 0.0;

			// If player rank is the (1st) worst rank, update finalBalance
			if (item.getRank() == rankCount.get(0).getRank()) {
				pot = percentage * loserPot;
				item.setFinalBalance(item.getFinalBalance() + pot);
			}

			// Update loserPot
			item.setLoserPot(pot);

			// Update idEvent
			item.setIdEvent(idEvent);
		});
	}

	private static void distributeLoserPotUnequally(Long idEvent, List<Result> results, Double loserPot, List<RankCountProjection> rankCount) {
		// Smallest piece of loserPot equally divided among the 2nd worst-ranked players
		Double percentage = SECOND_WORST_RANK_LOSER_POT_PERCENTAGE / rankCount.get(1).getCount();

		// Loop to update loserPot and finalBalance
		results.forEach(item -> {
			// Non-worst-ranked players take 0% of loserPot
			Double pot = 0.0;

			// If player rank is the (1st) worst rank, update finalBalance
			if (item.getRank() == rankCount.get(0).getRank()) {
				pot = WORST_RANK_LOSER_POT_PERCENTAGE * loserPot;
				item.setFinalBalance(item.getFinalBalance() + pot);
			}
			// If player rank is the 2nd worst rank, update finalBalance
			else if (item.getRank() == rankCount.get(1).getRank()) {
				pot = percentage * loserPot;
				item.setFinalBalance(item.getFinalBalance() + pot);
			}

			// Update loserPot
			item.setLoserPot(pot);

			// Update idEvent
			item.setIdEvent(idEvent);
		});
	}

	private static void logResults(List<Result> results) {
		int maxNameLength = results.stream()
				.map(Result::getPlayerName)
				.filter(Objects::nonNull)
				.mapToInt(String::length)
				.max()
				.orElse(25); // fallback

		results.forEach(item -> 
		System.out.printf(
			// "\t{ rank: %-2d | name: %-25s | rankBalance: %8.2f | loserPot: %8.2f | finalBalance: %8.2f }%n",
			// "\t{ rank: %-2d | name: %-" + Player.NAME_LENGTH + "s | rankBalance: %8.2f | loserPot: %8.2f | finalBalance: %8.2f }%n",
			"\t{ rank: %-2d | name: %-" + maxNameLength + "s | rankBalance: %8.2f | loserPot: %8.2f | finalBalance: %8.2f }%n",
			item.getRank(),
			item.getPlayerName(),
			item.getRankBalance(),
			item.getLoserPot(),
			item.getFinalBalance()
			)
		);
	}

	public static List<Result> calculate(
            Long idEvent,
            List<RankProjection> ranks,
            LoserPotProjection loserPot,
            List<RankCountProjection> rankCount) {

	    if (ranks == null || ranks.isEmpty()) {
	        return Collections.emptyList();
	    }

	    List<Result> results = buildResults(idEvent, ranks);

	    if (loserPot == null || rankCount == null || rankCount.isEmpty()) {
	        return results;
	    }

	    distributeLoserPot(idEvent, results, loserPot, rankCount);

	    logResults(results);

	    return results;
	}
}