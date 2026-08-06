package br.com.gathering.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.gathering.entity.Round;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long>{

	List<Round> findByIdEvent(Long idEvent);

	Optional<Round> findByIdEventAndRound(Long idEvent, Integer round);

	List<Round> findByIdEventAndCanceledFalse(Long idEvent);

	@Modifying
	@Query(nativeQuery = true, value = """
	    UPDATE gathering.round
	       SET canceled = true
	    WHERE id_event = :idEvent
	""")
	void cancelByIdEvent(@Param("idEvent") Long idEvent);

}
