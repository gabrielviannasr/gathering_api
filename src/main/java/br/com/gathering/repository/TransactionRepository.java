package br.com.gathering.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.gathering.entity.Transaction;
import br.com.gathering.projection.gathering.PlayerWalletProjection;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

	Optional<Transaction> findById(Long id);

    void deleteByIdEvent(Long idEvent);
    
    List<Transaction> findByIdEvent(Long idEvent);

    @Query(nativeQuery = true, value = """
    	    SELECT
				-- id_gathering AS idGathering,
				-- gathering_name AS gatheringName,
				id_player AS idPlayer,
				player_name AS playerName,
				wallet
			FROM
				gathering.vw_gathering_player_wallet
			WHERE
				id_gathering = :idGathering
				AND id_player = :idPlayer 
    	""")
    PlayerWalletProjection getWalletBalance(@Param("idGathering") Long idGathering, @Param("idPlayer") Long idPlayer);

    // Future cross validation. Player must belong to the same transaction's gathering.
    @Query(nativeQuery = true, value = """
    		SELECT COUNT(p) > 0
			FROM gathering.player p
			JOIN gathering.gathering g ON g.id = :idGathering
			WHERE p.id = :idPlayer
    		""")
	boolean playerBelongsToGathering(Long idPlayer, Long idGathering);

}
