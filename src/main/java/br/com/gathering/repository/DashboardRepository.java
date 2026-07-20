package br.com.gathering.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.gathering.projection.RankProjection;
import br.com.gathering.projection.gathering.FormatProjection;
import br.com.gathering.projection.gathering.GatheringSummaryProjection;
import br.com.gathering.projection.gathering.PlayerTransactionProjection;
import br.com.gathering.projection.gathering.PlayerWalletProjection;
import br.com.gathering.projection.gathering.ResultProjection;

@Repository
public interface DashboardRepository extends ViewRepository {

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
			ORDER BY
				id_gathering, player_name
    	""")
    List<PlayerWalletProjection> getWalletBalance(@Param("idGathering") Long idGathering);
    
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
			ORDER BY
				id_gathering, player_name
    	""")
	Optional<PlayerWalletProjection> getWalletBalance(
			@Param("idGathering") Long idGathering,
			@Param("idPlayer") Long idPlayer);

    @Query(nativeQuery = true, value = """
    	    SELECT
				-- id_gathering AS idGathering,
				-- gathering_name AS gatheringName,
				id_event AS idEvent,
				id_player AS idPlayer,
				player_name AS playerName,
				id_transaction AS idTransaction,
				created_at AS createdAt,
				transaction_type_name AS transactionTypeName,
				amount,
				transaction_description AS transactionDescription
			FROM
				gathering.vw_gathering_player_transaction
			WHERE
				id_gathering = :idGathering
			ORDER BY
				id_gathering, player_name
    	""")
	List<PlayerTransactionProjection> getPlayerTransaciton(@Param("idGathering") Long idGathering);
    
    @Query(nativeQuery = true, value = """
			SELECT
				rank,
				id_player AS idPlayer,
				player_name AS playerName,
				wins,
				rounds,
				positive,
				negative,
				rank_balance AS rankBalance
			FROM
			    gathering.vw_gathering_player_rank
			WHERE
				id_gathering = :idGathering
			""")
	List<RankProjection> getRankProjection(@Param("idGathering") Long idGathering);
    
    @Query(nativeQuery = true, value = """
			SELECT
				id_gathering AS idGathering,
				gathering_name AS gatheringName,
				id_format AS idFormat,
				format_name AS formatName,
				rounds
			FROM
				gathering.vw_gathering_format
			WHERE
				id_gathering = :idGathering
			""")
	List<FormatProjection> getFormatProjection(@Param("idGathering") Long idGathering);

    @Query(nativeQuery = true, value = """
    	    SELECT
		        id_gathering AS idGathering,
		        gathering_name AS gatheringName,
		        id_player AS idPlayer,
		        player_name AS playerName,
		        rank,
		        events,
		        wins,
		        rounds,
		        positive,
		        negative,
		        rank_balance AS rankBalance,
		        loser_pot AS loserPot,
		        confra_pot AS confraPot,
		        final_balance AS finalBalance
		    FROM
		        gathering.vw_gathering_result
		    WHERE
		        id_gathering = :idGathering
			""")
    List<ResultProjection> getResultProjection(@Param("idGathering") Long idGathering);

    @Query(nativeQuery = true, value = """
    	    SELECT
		        id_gathering AS idGathering,
		        gathering_name AS gatheringName,
		        id_player AS idPlayer,
		        player_name AS playerName,
		        rank,
		        events,
		        wins,
		        rounds,
		        positive,
		        negative,
		        rank_balance AS rankBalance,
		        loser_pot AS loserPot,
		        confra_pot AS confraPot,
		        final_balance AS finalBalance
		    FROM
		        gathering.vw_gathering_result
		    WHERE
		        id_gathering = :idGathering
		        AND id_player = :idPlayer
			""")
	Optional<ResultProjection> getResultProjection(
	    @Param("idGathering") Long idGathering,
	    @Param("idPlayer") Long idPlayer);

    @Query(nativeQuery = true, value = """
			SELECT
				id_gathering AS idGathering,
				gathering_name AS gatheringName,
				events,
				players,
				rounds,
				loser_pot AS loserPot,
				confra_pot AS confraPot,
				prize
			FROM
				gathering.vw_gathering_summary
			WHERE
				id_gathering = :idGathering
			""")
	GatheringSummaryProjection getSummaryProjection(@Param("idGathering") Long idGathering);

}
