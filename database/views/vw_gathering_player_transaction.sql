CREATE OR REPLACE VIEW gathering.vw_gathering_player_transaction AS
SELECT
    t.id_gathering,
    g.name AS gathering_name,
    t.id_event,
    t.id_player,
    p.name AS player_name,
    t.id AS id_transaction,
    t.created_at,
    tt.id AS id_transaction_type,
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

COMMENT ON VIEW gathering.vw_gathering_player_transaction IS
'Exibe as transações de cada jogador em suas respectivas confraternizações, incluindo tipo, valor e descrição. 
A ordenação segue a sequência natural do fluxo: Inscrição → Resultado → Depósito → Saque.';