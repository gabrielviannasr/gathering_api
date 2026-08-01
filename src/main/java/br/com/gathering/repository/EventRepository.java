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
		        players,
		        rounds,
		        loser_pot AS loserPot,
		        confra_pot AS confraPot,
		        prize
		    FROM gathering.vw_event_summary
		    WHERE id_event = :idEvent
	    """)
	EventRefreshProjection getRefreshProjection(@Param("idEvent") Long idEvent);

}
