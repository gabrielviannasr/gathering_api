/* CREATE SEQUENCES */
CREATE SEQUENCE gathering.sequence_event START 1;
CREATE SEQUENCE gathering.sequence_event_fee START 1;
CREATE SEQUENCE gathering.sequence_format START 1;
CREATE SEQUENCE gathering.sequence_gathering START 1;
CREATE SEQUENCE gathering.sequence_player START 1;
CREATE SEQUENCE gathering.sequence_result START 1;
CREATE SEQUENCE gathering.sequence_round START 1;
CREATE SEQUENCE gathering.sequence_round_player START 1;
CREATE SEQUENCE gathering.sequence_transaction START 1;
CREATE SEQUENCE gathering.sequence_transaction_type START 1;
/* CREATE SEQUENCES */

/* CREATE TABLES */
-- 🧍‍♂️ Tabela de jogadores / participantes
CREATE TABLE gathering.player (
    id INT DEFAULT nextval('gathering.sequence_player'::regclass) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

COMMENT ON TABLE gathering.player IS
'Representa um participante das conferências (gatherings).
Detalhes pessoais e financeiros são tratados em entidades relacionadas.';

-- 🏆 Tabela principal de confras
CREATE TABLE gathering.gathering (
    id INT DEFAULT nextval('gathering.sequence_gathering'::regclass) PRIMARY KEY,
	id_player INT NOT NULL, -- createdBy and the person in charge of the event
	year INT DEFAULT EXTRACT(YEAR FROM CURRENT_DATE),
    name VARCHAR(20),

	CONSTRAINT fk_gathering_player FOREIGN KEY (id_player) REFERENCES gathering.player(id)
);

COMMENT ON TABLE gathering.gathering IS
'Representa uma confras (gathering), ou conjunto de eventos de um grupo de jogadores.
Cada gathering é criada e gerenciada por um jogador responsável (id_player).';

-- 🧩 Formatos de jogo
CREATE TABLE gathering.format (
    id INT DEFAULT nextval('gathering.sequence_format'::regclass) PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    life_count INT NOT NULL
);

COMMENT ON TABLE gathering.format IS
'Define os formatos de jogo disponíveis para os eventos, incluindo os pontos de vidas (life_count) associado a cada formato.';

-- 🎯 Tabela de eventos
CREATE TABLE gathering.event (
    id INT DEFAULT nextval('gathering.sequence_event'::regclass) PRIMARY KEY,
    id_gathering INT NOT NULL,
    id_format INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    players INT NOT NULL DEFAULT 0,
    rounds INT NOT NULL DEFAULT 0,
    confra_fee NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (confra_fee >= 0),
    round_fee NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (round_fee >= 0),
    loser_fee4 NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (loser_fee4 >= 0),
    loser_fee5 NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (loser_fee5 >= 0),
    loser_fee6 NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (loser_fee6 >= 0),    
    loser_pot NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (loser_pot >= 0),
    confra_pot NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (confra_pot >= 0),
    prize NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (prize >= 0),

	CONSTRAINT fk_event_gathering FOREIGN KEY (id_gathering) REFERENCES gathering.gathering(id),
    CONSTRAINT fk_event_format FOREIGN KEY (id_format) REFERENCES gathering.format(id)
);

COMMENT ON TABLE gathering.event IS
'Representa um evento individual pertencente a uma confra (gathering).
Armazena informações sobre taxas, rodadas, valores acumulados e premiações.';

COMMENT ON COLUMN gathering.event.id_gathering IS 'Identificador da confra à qual o evento pertence.';

COMMENT ON COLUMN gathering.event.id_format IS 'Formato de jogo associado ao evento.';

COMMENT ON COLUMN gathering.event.confra_fee IS 'Taxa destinada ao pote da confra.';

COMMENT ON COLUMN gathering.event.round_fee IS 'Taxa de inscrição cobrada em cada rodada do evento.';

COMMENT ON COLUMN gathering.event.loser_fee4 IS 'Taxa destinada ao pote dos derrotados quando a rodada tiver 4 jogadores.';

COMMENT ON COLUMN gathering.event.loser_fee5 IS 'Taxa destinada ao pote dos derrotados quando a rodada tiver 5 jogadores.';

COMMENT ON COLUMN gathering.event.loser_fee6 IS 'Taxa destinada ao pote dos derrotados quando a rodada tiver 6  jogadores.';

COMMENT ON COLUMN gathering.event.loser_pot IS 'Total acumulado destinado ao pote dos derrotados.';

COMMENT ON COLUMN gathering.event.confra_pot IS 'Total acumulado destinado ao pote da confra.';

COMMENT ON COLUMN gathering.event.prize IS 'Total acumulado de premiação do evento (pote dos vencedores).';

CREATE TABLE gathering.event_fee (
    id INT DEFAULT nextval('gathering.sequence_event_fee'::regclass) PRIMARY KEY,
    id_event INT NOT NULL,
    players INT NOT NULL CHECK (players >= 0),
    prize_fee NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (prize_fee >= 0),
    loser_fee NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (loser_fee >= 0),

    CONSTRAINT fk_event_fee_event FOREIGN KEY (id_event) REFERENCES gathering.event(id),

    CONSTRAINT uq_event_fee_event_players UNIQUE (id_event, players)
);

COMMENT ON TABLE gathering.event_fee IS
'Armazena as taxas de distribuição de valores (fees) configuradas para cada evento,
de forma dinâmica por quantidade de jogadores.
Cada registro define quanto do valor total arrecadado é destinado aos vencedores (prize_fee)
e quanto vai para o pote dos derrotados (loser_fee).';

COMMENT ON COLUMN gathering.event_fee.players IS
'Número de jogadores na rodada que determina o valor da taxa de distribuição.';

COMMENT ON COLUMN gathering.event_fee.prize_fee IS
'Parcela do valor total da rodada destinada aos vencedores.';

COMMENT ON COLUMN gathering.event_fee.loser_fee IS
'Parcela do valor total da rodada destinada ao pote dos derrotados.';

-- 🧭 Tabela de rodadas
CREATE TABLE gathering.round (
    id INT DEFAULT nextval('gathering.sequence_round'::regclass) PRIMARY KEY,
    id_event INT NOT NULL,
    id_format INT NOT NULL,
    id_player_winner INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    round INT NOT NULL,
    players INT NOT NULL DEFAULT 0 CHECK (players >= 0),
    prize NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (prize >= 0),
    loser_pot NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (loser_pot >= 0),
    canceled BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT fk_round_event FOREIGN KEY (id_event) REFERENCES gathering.event(id),
    CONSTRAINT fk_round_format FOREIGN KEY (id_format) REFERENCES gathering.format(id),
    CONSTRAINT fk_round_player_winner FOREIGN KEY (id_player_winner) REFERENCES gathering.player(id),

    CONSTRAINT uq_round_event_round UNIQUE (id_event, round)
);

COMMENT ON TABLE gathering.round IS
'Representa uma rodada dentro de um evento.
Armazena informações sobre formato, jogadores, vencedor, prêmiação e valores destinados ao pote dos derrotados.';

COMMENT ON COLUMN gathering.round.id_event IS 'Identificador do evento ao qual a rodada pertence.';

COMMENT ON COLUMN gathering.round.id_format IS 'Formato de jogo utilizado nesta rodada.';

COMMENT ON COLUMN gathering.round.id_player_winner IS 'Identificador do jogador vencedor da rodada.';

COMMENT ON COLUMN gathering.round.created_at IS 'Data e hora de criação da rodada.';

COMMENT ON COLUMN gathering.round.round IS 'Número sequencial da rodada dentro do evento.';

COMMENT ON COLUMN gathering.round.players IS 'Quantidade de jogadores participantes da rodada.';

COMMENT ON COLUMN gathering.round.prize IS 'Valor total de premiação entregue nesta rodada.';

COMMENT ON COLUMN gathering.round.loser_pot IS 'Valor total destinado ao pote dos derrotados nesta rodada.';

COMMENT ON COLUMN gathering.round.canceled IS 'Indica se a rodada foi cancelada (true) ou válida (false).';

-- 🧮 Tabela de placar por rodada (Round_Player)
CREATE TABLE gathering.round_player (
    id_round INT NOT NULL,
    id_player INT NOT NULL,

    CONSTRAINT pk_round_player PRIMARY KEY (id_round, id_player),
    CONSTRAINT fk_round_player_round FOREIGN KEY (id_round) REFERENCES gathering.round(id),
    CONSTRAINT fk_round_player_player FOREIGN KEY (id_player) REFERENCES gathering.player(id)
);

COMMENT ON TABLE gathering.round_player IS
'Armazena a participação dos jogadores em cada rodada.
Cada registro vincula um jogador a uma rodada específica, garantindo uma única entrada por jogador por rodada.';

COMMENT ON COLUMN gathering.round_player.id_round IS 'Identificador da rodada.';

COMMENT ON COLUMN gathering.round_player.id_player IS 'Identificador do jogador participante da rodada.';

-- ======================================================
-- 🏁 Tabela de resultados (Result)
-- Armazena o desempenho final de cada jogador em um evento,
-- incluindo posição no ranking, estatísticas de rodadas e
-- saldos antes e depois da distribuição do pote dos derrotados.
-- ======================================================
CREATE TABLE gathering.result (
    id INT DEFAULT nextval('gathering.sequence_result'::regclass) PRIMARY KEY,
    id_event INT NOT NULL,
    id_player INT NOT NULL,
    rank INT,
    wins INT NOT NULL DEFAULT 0 CHECK (wins >= 0),
    rounds INT NOT NULL DEFAULT 0 CHECK (rounds >= 0),
    positive NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (positive >= 0), -- total earned
    negative NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (negative >= 0), -- total owed
    rank_balance NUMERIC(10,2) NOT NULL DEFAULT 0, -- net result before pot distribution
    loser_pot NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (loser_pot >= 0), -- share of loser pot
    final_balance NUMERIC(10,2) NOT NULL DEFAULT 0, -- final result after pot distribution

    CONSTRAINT fk_result_event FOREIGN KEY (id_event) REFERENCES gathering.event(id),
    CONSTRAINT fk_result_player FOREIGN KEY (id_player) REFERENCES gathering.player(id),

    CONSTRAINT uq_result_event_player UNIQUE (id_event, id_player) -- prevents duplicates
);

COMMENT ON TABLE gathering.result IS
'Armazena o resultado final de cada jogador em um evento, incluindo sua colocação, desempenho nas rodadas e saldo final após a distribuição do pote dos derrotados.';

COMMENT ON COLUMN gathering.result.id_event IS 'Identificador do evento ao qual o resultado pertence.';

COMMENT ON COLUMN gathering.result.id_player IS 'Identificador do jogador ao qual o resultado pertence.';

COMMENT ON COLUMN gathering.result.rank IS 'Posição final do jogador no ranking do evento.';

COMMENT ON COLUMN gathering.result.wins IS 'Número total de vitórias obtidas pelo jogador no evento.';

COMMENT ON COLUMN gathering.result.rounds IS 'Número total de rodadas disputadas pelo jogador no evento.';

COMMENT ON COLUMN gathering.result.positive IS 'Valor total recebido pelo jogador (ganhos acumulados).';

COMMENT ON COLUMN gathering.result.negative IS 'Valor total pago ou devido pelo jogador (custos acumulados).';

COMMENT ON COLUMN gathering.result.rank_balance IS 'Saldo líquido do jogador antes da distribuição do pote dos derrotados (positivo = lucro, negativo = perda).';

COMMENT ON COLUMN gathering.result.loser_pot IS 'Parcela do pote dos derrotados recebida pelo jogador.';

COMMENT ON COLUMN gathering.result.final_balance IS 'Saldo final do jogador após a distribuição do pote dos derrotados.';

-- 💰 Tipos de transações financeiras
CREATE TABLE gathering.transaction_type (
    id INT DEFAULT nextval('gathering.sequence_transaction_type'::regclass) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(100)
);

COMMENT ON TABLE gathering.transaction_type IS
'Define os tipos de transações financeiras disponíveis no sistema.
Cada tipo representa uma operação específica do jogador, como depósito, saque ou taxas de evento.';

-- 💸 Tabela de transações financeiras
CREATE TABLE gathering.transaction (
    id INT DEFAULT nextval('gathering.sequence_transaction'::regclass) PRIMARY KEY,
    id_gathering INT NOT NULL,
    id_event INT NULL,
    id_player INT NOT NULL,    
    id_transaction_type INT NOT NULL,    
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount NUMERIC(10,2) NOT NULL,
	description VARCHAR(500),
    CONSTRAINT fk_transaction_gathering FOREIGN KEY (id_gathering) REFERENCES gathering.gathering(id),
    CONSTRAINT fk_transaction_event FOREIGN KEY (id_event) REFERENCES gathering.event(id),
    CONSTRAINT fk_transaction_player FOREIGN KEY (id_player) REFERENCES gathering.player(id),
    CONSTRAINT fk_transaction_transaction_type FOREIGN KEY (id_transaction_type) REFERENCES gathering.transaction_type(id)
);

COMMENT ON TABLE gathering.transaction IS 
'Armazena todas as transações financeiras realizadas pelos jogadores.
Um valor positivo representa crédito, enquanto um valor negativo representa débito.
O campo id_event é opcional e indica o evento que originou a transação, se aplicável.';
/* CREATE TABLES */

/* CREATE VIEWS */
CREATE OR REPLACE VIEW gathering.vw_event_confra_pot AS
    SELECT
        e.id_gathering,
        g.name AS gathering_name,
        e.id AS id_event,
        COUNT(DISTINCT s.id_player) AS players,
        COUNT(DISTINCT s.id_player) * e.confra_fee AS confra_pot
    FROM
        gathering.round_player s
        INNER JOIN gathering.round r ON r.id = s.id_round
        INNER JOIN gathering.player p ON p.id = s.id_player
        INNER JOIN gathering.event e ON e.id = r.id_event
        INNER JOIN gathering.gathering g ON g.id = e.id_gathering
    WHERE
        r.canceled = false
    GROUP BY
        g.id, g.name, e.id;

COMMENT ON VIEW gathering.vw_event_confra_pot IS
'Exibe o total de jogadores e o valor acumulado destinado ao pote da confra em cada evento.';

CREATE OR REPLACE VIEW gathering.vw_event_loser_pot AS
    SELECT
        g.id AS id_gathering,
        g.name AS gathering_name,
        e.id AS id_event,
        COUNT(r.id) AS rounds,
        SUM(r.loser_pot) AS loser_pot,
        SUM(r.prize) AS prize
    FROM
        gathering.round r
        INNER JOIN gathering.event e ON e.id = r.id_event
        INNER JOIN gathering.gathering g ON g.id = e.id_gathering
    WHERE
        r.canceled = false
    GROUP BY
        g.id, e.id;

COMMENT ON VIEW gathering.vw_event_loser_pot IS
'Exibe o total de rodadas, o valor total de premiações e o valor acumulado no pote dos derrotados por evento.';

CREATE OR REPLACE VIEW gathering.vw_event_player_balance AS
    WITH player_balance AS (
        SELECT
            e.id_gathering,
            g.name AS gathering_name,
            e.id AS id_event,
            p.id AS id_player,
            p.name AS player_name,
            COUNT(CASE WHEN r.id_player_winner = p.id THEN 1 END) AS wins,
            COUNT(s.id_player) AS rounds,
            COALESCE(SUM(r.prize) FILTER (WHERE r.id_player_winner = p.id), 0) AS positive,
            -- SUM(CASE WHEN r.id_player_winner = p.id THEN r.prize ELSE 0 END) AS positive,
            COUNT(s.id_player) * e.round_fee AS negative
        FROM
            gathering.round_player s
            INNER JOIN gathering.round r ON r.id = s.id_round
            INNER JOIN gathering.player p ON p.id = s.id_player
            INNER JOIN gathering.event e ON e.id = r.id_event
            INNER JOIN gathering.gathering g ON g.id = e.id_gathering
        WHERE
            r.canceled = false
        GROUP BY
    --	    e.id, p.id
    --		Garante portabilidade e evita warning em versões futuras.
            g.id, e.id, p.id, p.name, e.round_fee
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

COMMENT ON VIEW gathering.vw_event_player_balance IS
'Exibe o desempenho e o saldo de cada jogador em um evento,
incluindo número de vitórias, rodadas, premiação, taxas e saldo resultante (rank balance).';

CREATE OR REPLACE VIEW gathering.vw_event_player_rank AS
    SELECT
        id_gathering,
        gathering_name,
        id_event,
        RANK() OVER (
            -- PARTITION BY id_event garante que o ranking é calculado dentro de cada evento individualmente.
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

COMMENT ON VIEW gathering.vw_event_player_rank IS
'Apresenta o ranking dos jogadores em cada evento, calculado com base no saldo (rank balance).';

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

COMMENT ON VIEW gathering.vw_event_rank_count IS
'Indica quantos jogadores ocupam cada posição no ranking, utilizada para a distribuição do pote dos derrotados.';

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

COMMENT ON VIEW gathering.vw_event_summary IS
'Apresenta um resumo consolidado de cada evento, unindo informações do pote da confra e do pote dos derrotados.
Inclui o total de jogadores, rodadas, valores acumulados e premiações do evento.';

CREATE OR REPLACE VIEW gathering.vw_gathering_format AS
    SELECT
        g.id AS id_gathering,
        g.name AS gathering_name,
        f.id AS id_format,
        f.name AS format_name,
        COUNT(r.id) AS rounds
    FROM
        gathering.gathering g
        INNER JOIN gathering.event e ON e.id_gathering = g.id
        INNER JOIN gathering.round r ON r.id_event = e.id
        INNER JOIN gathering.format f ON r.id_format = f.id
    WHERE
        r.canceled = false
    GROUP BY
        g.id, f.id
    ORDER BY
        g.name, f.name;

COMMENT ON VIEW gathering.vw_gathering_format IS
'Exibe o total de rodadas jogadas de cada formato em uma confra.';

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

COMMENT ON VIEW gathering.vw_gathering_player_balance IS
'Agrega os saldos dos jogadores considerando todos os eventos de uma confra.
Serve como base para o cálculo do ranking acumulado em nível de confra.';

CREATE OR REPLACE VIEW gathering.vw_gathering_player_rank AS
    SELECT
        id_gathering,
        gathering_name,
        RANK() OVER (
            -- PARTITION BY id_gathering garante que o ranking é calculado dentro de cada gathering individualmente.
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

COMMENT ON VIEW gathering.vw_gathering_player_rank IS
'Apresenta o ranking dos jogadores dentro de cada confra, 
com base no desempenho e saldo acumulado, derivado da view vw_gathering_player_balance.';

CREATE OR REPLACE VIEW gathering.vw_gathering_player_transaction AS
    SELECT
        t.id_gathering,
        g.name AS gathering_name,
        t.id_event,
        t.id_player,
        p.name AS player_name,
        t.id AS id_transaction,
        t.created_at,
        tt.name AS transaction_type_name,
        t.amount,
        t.description AS transaction_description
    FROM
        gathering.gathering g
    LEFT JOIN
        gathering.transaction t ON t.id_gathering = g.id
    LEFT JOIN
        gathering.player p ON p.id = t.id_player
    LEFT JOIN
        gathering.transaction_type tt ON tt.id = t.id_transaction_type
    ORDER BY
        g.name, p.name, t.created_at, t.id_transaction_type;

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

COMMENT ON VIEW gathering.vw_gathering_summary IS
'Apresenta um resumo consolidado de cada confra (gathering),
incluindo a quantidade total de eventos, jogadores, rodadas,
e os valores acumulados dos potes (dos derrotados e da confra),
além do total de premiações.

Os dados são derivados das views de evento, garantindo consistência
e cálculos sempre atualizados.';

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
/* CREATE VIEWS */

/* CREATE INDEXES */
-- 🧱 Tabela gathering.event
-- 🔹 Usadas em joins de praticamente todas as views (vw_event_*, vw_gathering_*).
CREATE INDEX IF NOT EXISTS idx_event_id_gathering ON gathering.event(id_gathering);
CREATE INDEX IF NOT EXISTS idx_event_id_format ON gathering.event(id_format);

-- 🧱 Tabela gathering.round
-- 🔹 Importantes para relacionar rounds → events e rounds → winners nas views de performance.
CREATE INDEX IF NOT EXISTS idx_round_id_event ON gathering.round(id_event);
CREATE INDEX IF NOT EXISTS idx_round_id_player_winner ON gathering.round(id_player_winner);

-- 🧱 Tabela gathering.round_player 
-- 🔹 Usadas em vw_event_player_balance, base do ranking e do resumo.
CREATE INDEX IF NOT EXISTS idx_round_player_id_round ON gathering.round_player (id_round);
CREATE INDEX IF NOT EXISTS idx_round_player_id_player ON gathering.round_player (id_player);

-- 🧱 Tabela gathering.transaction
-- 🔹 Essencial para as views de saldo (vw_gathering_player_wallet) e consultas de movimentação.
CREATE INDEX IF NOT EXISTS idx_transaction_id_gathering ON gathering.transaction(id_gathering);
CREATE INDEX IF NOT EXISTS idx_transaction_id_event ON gathering.transaction(id_event);
CREATE INDEX IF NOT EXISTS idx_transaction_id_player ON gathering.transaction(id_player);
CREATE INDEX IF NOT EXISTS idx_transaction_type ON gathering.transaction(id_transaction_type);
-- 🔹 (Opcional) índice composto para carteiras (melhor em joins por gathering + player).
-- Isso substitui os dois índices separados (id_gathering, id_player) em alguns casos,
-- então se quiser ser minimalista, pode manter só o composto.
CREATE INDEX IF NOT EXISTS idx_transaction_gathering_player ON gathering.transaction (id_gathering, id_player);

-- 🧱 Tabela gathering.result
CREATE INDEX IF NOT EXISTS idx_result_event_player ON gathering.result(id_event, id_player);

-- 🧱 Tabela gathering.player
-- Opcional — só crie se:
--  🔹você faz busca de jogadores por nome (WHERE name ILIKE 'gabriel%');
--  🔹ou ordena listas grandes de jogadores por nome com frequência.
CREATE INDEX IF NOT EXISTS idx_gathering_player_name ON gathering.player(name);
-- CREATE INDEX IF NOT EXISTS idx_gathering_player_email ON gathering.player(email);
-- CREATE INDEX IF NOT EXISTS idx_gathering_player_username ON gathering.player(username);
/* CREATE INDEXES */
