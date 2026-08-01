SELECT
    COUNT(DISTINCT rp.id_player) AS players,
    COUNT(DISTINCT r.id) AS rounds
FROM gathering.round r
LEFT JOIN gathering.round_player rp
    ON rp.id_round = r.id
WHERE r.id_event = :idEvent
    AND r.canceled = false;