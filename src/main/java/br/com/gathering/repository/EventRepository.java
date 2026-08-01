package br.com.gathering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.gathering.entity.Event;
import br.com.gathering.projection.EventRefreshProjection;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{

	@Query(nativeQuery = true, value = """
		SELECT
		    COUNT(DISTINCT rp.id_player) AS players,
			COUNT(DISTINCT r.id) AS rounds
		FROM gathering.round r
		LEFT JOIN gathering.round_player rp
			ON rp.id_round = r.id
		WHERE r.id_event = :idEvent
			AND r.canceled = false
	""")
	EventRefreshProjection getRefreshProjection(@Param("idEvent") Long idEvent);

}
