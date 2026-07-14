package br.com.gathering.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.gathering.entity.Gathering;

@Repository
public interface GatheringRepository extends JpaRepository<Gathering, Long>{

	@Query("SELECT DISTINCT g.year FROM Gathering g ORDER BY g.year DESC")
    List<Integer> findAvailableYears();

}
