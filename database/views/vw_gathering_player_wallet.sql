CREATE OR REPLACE VIEW gathering.vw_gathering_player_wallet AS
    SELECT
        g.id AS id_gathering,
        g.name AS gathering_name,
        p.id AS id_player,
        p.name AS player_name,
        COUNT(DISTINCT t.id_event) FILTER (WHERE t.id_player = p.id) AS events,
        COALESCE(SUM(t.amount), 0) AS wallet
    FROM
        gathering.player p
    LEFT JOIN
        gathering.transaction t ON t.id_player = p.id
    LEFT JOIN
        gathering.gathering g ON g.id = t.id_gathering
    GROUP BY
        g.id, g.name, p.id, p.name
    ORDER BY
        g.name, p.name;

COMMENT ON VIEW gathering.vw_gathering_player_wallet IS
'Exibe o saldo (carteira) de cada jogador agrupado por confra, 
calculado a partir de todas as transações relacionadas.';