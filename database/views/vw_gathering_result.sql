CREATE OR REPLACE VIEW gathering.vw_gathering_result AS
WITH player_final_balance AS(
	SELECT
		g.id AS id_gathering,
	    g.name AS gathering_name,
		p.id AS id_player,
		p.name AS player_name,
		COUNT(r.id) AS events,
		COALESCE(SUM(r.wins), 0) AS wins,
	 	COALESCE(SUM(r.rounds), 0) AS rounds,
	    COALESCE(SUM(r.positive), 0) AS positive,
	    COALESCE(SUM(r.negative), 0) AS negative,
	    COALESCE(SUM(r.rank_balance), 0) AS rank_balance,
	    COALESCE(SUM(r.loser_pot), 0) AS loser_pot,
	    -COALESCE(SUM(e.confra_fee), 0) AS confra_pot
--	    ,COALESCE(SUM(r.final_balance), 0) AS final_balance
	FROM
		gathering.result r
		INNER JOIN gathering.event e ON e.id = r.id_event
		INNER JOIN gathering.gathering g ON g.id = e.id_gathering
		INNER JOIN gathering.player p ON p.id = r.id_player
	GROUP BY
		g.id, p.id
)
SELECT
	id_gathering,
	gathering_name,
	id_player,
	player_name,
	RANK() OVER (
        PARTITION BY id_gathering
        ORDER BY (rank_balance) DESC, rounds ASC
        -- ORDER BY (rank_balance + loser_pot + confra_pot) DESC, rounds ASC
    ) AS rank,
	events,
	wins,
	rounds,
	positive,
	negative,
	rank_balance,
	loser_pot,
	confra_pot,
	rank_balance + loser_pot + confra_pot AS final_balance
FROM
	player_final_balance
ORDER BY
	gathering_name, rank, player_name;

COMMENT ON VIEW gathering.vw_gathering_result IS
'Apresenta o resultado consolidado de cada jogador em toda a confra (gathering).

A view soma os desempenhos individuais de cada jogador em todos os eventos da confra,
incluindo vitórias, rodadas jogadas, prêmios ganhos e taxas pagas.

O campo final_balance representa o saldo líquido final do jogador,
calculado como a soma do rank_balance com os valores recebidos do loser_pot
e descontadas as taxas de confra (confra_pot / confra_fee).

O ranking é calculado por confra (id_gathering), ordenando pelo saldo final (final_balance)
em ordem decrescente e, em caso de empate, pela menor quantidade de rodadas jogadas.';