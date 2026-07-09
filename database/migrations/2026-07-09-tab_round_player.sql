BEGIN;

-- ======================================================
-- 1. Remove views dependentes da tabela gathering.score
-- ======================================================

DROP VIEW IF EXISTS gathering.vw_gathering_result;
DROP VIEW IF EXISTS gathering.vw_gathering_summary;
DROP VIEW IF EXISTS gathering.vw_gathering_player_rank;
DROP VIEW IF EXISTS gathering.vw_gathering_player_balance;
DROP VIEW IF EXISTS gathering.vw_event_summary;
DROP VIEW IF EXISTS gathering.vw_event_confra_pot;
DROP VIEW IF EXISTS gathering.vw_event_rank_count;
DROP VIEW IF EXISTS gathering.vw_event_player_rank;
DROP VIEW IF EXISTS gathering.vw_event_player_balance;


-- ======================================================
-- 2. Renomeia a tabela
-- ======================================================

ALTER TABLE gathering.score
RENAME TO round_player;


-- ======================================================
-- 3. Remove a PK antiga baseada no campo id
-- ======================================================

ALTER TABLE gathering.round_player
DROP CONSTRAINT score_pkey;


-- ======================================================
-- 4. Remove a UNIQUE antiga
-- Ela se torna desnecessária porque a combinação
-- (id_round, id_player) será a própria PRIMARY KEY
-- ======================================================

ALTER TABLE gathering.round_player
DROP CONSTRAINT uq_score_round_player;


-- ======================================================
-- 5. Remove a coluna id
-- ======================================================

ALTER TABLE gathering.round_player
DROP COLUMN id;


-- ======================================================
-- 6. Cria a nova chave primária composta
-- ======================================================

ALTER TABLE gathering.round_player
ADD CONSTRAINT pk_round_player
PRIMARY KEY (id_round, id_player);

-- ======================================================
-- 7. Renomeia as foreign keys
-- ======================================================

ALTER TABLE gathering.round_player
RENAME CONSTRAINT fk_score_round TO fk_round_player_round;

ALTER TABLE gathering.round_player
RENAME CONSTRAINT fk_score_player TO fk_round_player_player;

-- ======================================================
-- 8. Renomeia os índices existentes
-- ======================================================

ALTER INDEX gathering.idx_score_id_round
RENAME TO idx_round_player_id_round;

ALTER INDEX gathering.idx_score_id_player
RENAME TO idx_round_player_id_player;


-- ======================================================
-- 9. Remove a sequence que não é mais necessária
-- ======================================================

DROP SEQUENCE IF EXISTS gathering.sequence_score;


-- ======================================================
-- 10. Recria as views
-- ======================================================

CREATE OR REPLACE VIEW gathering.vw_event_confra_pot AS
SELECT
    e.id_gathering,
    g.name AS gathering_name,
    e.id AS id_event,
    COUNT(DISTINCT rp.id_player) AS players,
    COUNT(DISTINCT rp.id_player) * e.confra_fee AS confra_pot
FROM
    gathering.round_player rp
    INNER JOIN gathering.round r ON r.id = rp.id_round
    INNER JOIN gathering.player p ON p.id = rp.id_player
    INNER JOIN gathering.event e ON e.id = r.id_event
    INNER JOIN gathering.gathering g ON g.id = e.id_gathering
WHERE
    r.canceled = false
GROUP BY
    g.id, g.name, e.id;


CREATE OR REPLACE VIEW gathering.vw_event_player_balance AS
WITH player_balance AS (
    SELECT
        e.id_gathering,
        g.name AS gathering_name,
        e.id AS id_event,
        p.id AS id_player,
        p.name AS player_name,
        COUNT(CASE WHEN r.id_player_winner = p.id THEN 1 END) AS wins,
        COUNT(rp.id_player) AS rounds,
        COALESCE(
            SUM(r.prize) FILTER (WHERE r.id_player_winner = p.id),
            0
        ) AS positive,
        COUNT(rp.id_player) * e.round_fee AS negative
    FROM
        gathering.round_player rp
        INNER JOIN gathering.round r ON r.id = rp.id_round
        INNER JOIN gathering.player p ON p.id = rp.id_player
        INNER JOIN gathering.event e ON e.id = r.id_event
        INNER JOIN gathering.gathering g ON g.id = e.id_gathering
    WHERE
        r.canceled = false
    GROUP BY
        g.id,
        e.id,
        p.id,
        p.name,
        e.round_fee
)
SELECT
    id_gathering,
    gathering_name,
    id_event,
    id_player,
    player_name,
    wins,
    rounds,
    positive,
    negative,
    positive - negative AS rank_balance
FROM
    player_balance
ORDER BY
    player_name;

COMMIT;

-- ======================================================
-- 11. Recria as demais views
-- ======================================================

CREATE OR REPLACE VIEW gathering.vw_event_player_rank AS
    SELECT
        id_gathering,
        gathering_name,
        id_event,
        RANK() OVER (
            PARTITION BY id_event
            ORDER BY rank_balance DESC, rounds ASC
        ) AS rank,
        id_player,
        player_name,
        wins,
        rounds,
        positive,
        negative,
        rank_balance
    FROM
        gathering.vw_event_player_balance
    ORDER BY
        id_event, rank, player_name;


CREATE OR REPLACE VIEW gathering.vw_event_rank_count AS
    SELECT
        id_gathering,
        id_event,
        rank,
        COUNT(*)
    FROM
        gathering.vw_event_player_rank
    GROUP BY
        id_gathering, id_event, rank
    ORDER BY
        rank DESC;


CREATE OR REPLACE VIEW gathering.vw_event_summary AS
    SELECT
        loser.id_gathering,
        loser.gathering_name,
        loser.id_event,
        confra.players,
        loser.rounds,
        loser.loser_pot,
        confra.confra_pot,
        loser.prize
    FROM
        gathering.vw_event_loser_pot loser
        INNER JOIN gathering.vw_event_confra_pot confra
            ON confra.id_event = loser.id_event
    ORDER BY
        loser.id_gathering, loser.id_event;


CREATE OR REPLACE VIEW gathering.vw_gathering_player_balance AS
    SELECT
        id_gathering,
        gathering_name,
        id_player,
        player_name,
        COUNT(id_event) AS events,
        COALESCE(SUM(wins), 0) AS wins,
        COALESCE(SUM(rounds), 0) AS rounds,
        COALESCE(SUM(positive), 0) AS positive,
        COALESCE(SUM(negative), 0) AS negative,
        COALESCE(SUM(rank_balance), 0) AS rank_balance
    FROM
        gathering.vw_event_player_balance
    GROUP BY
        id_gathering, gathering_name, id_player, player_name
    ORDER BY
        id_gathering, player_name;


CREATE OR REPLACE VIEW gathering.vw_gathering_player_rank AS
    SELECT
        id_gathering,
        gathering_name,
        RANK() OVER (
            PARTITION BY id_gathering
            ORDER BY rank_balance DESC, rounds ASC
        ) AS rank,
        id_player,
        player_name,
        events,
        wins,
        rounds,
        positive,
        negative,
        rank_balance
    FROM
        gathering.vw_gathering_player_balance
    ORDER BY
        id_gathering, rank, player_name;


CREATE OR REPLACE VIEW gathering.vw_gathering_summary AS
    SELECT
        id_gathering,
        gathering_name,
        COUNT(id_event) AS events,
        COALESCE(SUM(players), 0) AS players,
        COALESCE(SUM(rounds), 0) AS rounds,
        COALESCE(SUM(loser_pot), 0) AS loser_pot,
        COALESCE(SUM(confra_pot), 0) AS confra_pot,
        COALESCE(SUM(prize), 0) AS prize
    FROM
        gathering.vw_event_summary
    GROUP BY
        id_gathering, gathering_name
    ORDER BY
        gathering_name;


CREATE OR REPLACE VIEW gathering.vw_gathering_result AS
WITH player_final_balance AS (
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
        ORDER BY rank_balance DESC, rounds ASC
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

ROLLBACK;