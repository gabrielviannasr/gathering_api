package br.com.gathering.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gathering.entity.Round;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long>{

	List<Round> findByIdEvent(Long idEvent);

}
