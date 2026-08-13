package br.com.gathering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gathering.entity.EventFee;

@Repository
public interface EventFeeRepository extends JpaRepository<EventFee, Long>{

	void deleteByIdEvent(Long idEvent);

}
