ALTER TABLE gathering.event
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE gathering.event
ADD COLUMN results_at TIMESTAMP;

COMMENT ON COLUMN gathering.event.updated_at IS
'Data e hora da última alteração que impacta o cálculo do resultado do evento.';

COMMENT ON COLUMN gathering.event.results_at IS
'Data e hora da última geração do ranking/resultados persistidos do evento.';

COMMENT ON COLUMN gathering.event.created_at IS
'Data e hora de criação do evento.';

COMMENT ON COLUMN gathering.event.players IS
'Número de jogadores inscritos no evento.';

COMMENT ON COLUMN gathering.event.rounds IS
'Número de rodadas do evento.';