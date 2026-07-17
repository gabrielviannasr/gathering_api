package br.com.gathering.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.gathering.entity.Result;
import br.com.gathering.projection.RankProjection;
import br.com.gathering.projection.event.ConfraPotProjection;
import br.com.gathering.projection.event.EventSummaryProjection;
import br.com.gathering.projection.event.LoserPotProjection;
import br.com.gathering.projection.event.RankCountProjection;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    void deleteByIdEvent(Long idEvent);

    Optional<Result> findByIdEventAndIdPlayer(Long idEvent, Long idPlayer);
    
    List<Result> findByIdEvent(Long idEvent);

    @Query(nativeQuery = true, value = """
			SELECT
				id_event AS idEvent,
				players,
				confra_pot AS confraPot
			FROM
				gathering.vw_event_confra_pot
			WHERE
			    id_event = :idEvent
			""")
	ConfraPotProjection getConfraPot(@Param("idEvent") Long idEvent);

	@Query(nativeQuery = true, value = """
			SELECT
			    id_event AS idEvent,
			    rounds,
			    loser_pot AS loserPot,
			    prize
			FROM
			    gathering.vw_event_loser_pot
			WHERE
			    id_event = :idEvent
			""")
	LoserPotProjection getLoserPot(@Param("idEvent") Long idEvent);

	@Query(nativeQuery = true, value = """
			SELECT
			    id_event AS idEvent,
			    rank,
			    count
			FROM
			    gathering.vw_event_rank_count
			WHERE
			    id_event = :idEvent
			""")
	List<RankCountProjection> getRankCount(@Param("idEvent") Long idEvent);

	@Query(nativeQuery = true, value = """
			SELECT
				id_event AS idEvent,
				rank,
				id_player AS idPlayer,
				player_name AS playerName,
				wins,
				rounds,
				positive,
				negative,
				rank_balance AS rankBalance
			FROM
			    gathering.vw_event_player_rank
			WHERE
				id_event = :idEvent
			""")
	List<RankProjection> getRankProjection(@Param("idEvent") Long idEvent);

	@Query(nativeQuery = true, value = """
			SELECT
				id_gathering AS idGathering,
				gathering_name AS gatheringName,
				id_event AS idEvent,
				players,
				rounds,
				loser_pot AS loserPot,
				confra_pot AS confraPot,
				prize
			FROM
				gathering.vw_event_summary
			WHERE
				id_event = :idEvent
			""")
	EventSummaryProjection getSummaryProjection(@Param("idEvent") Long idEvent);

}
