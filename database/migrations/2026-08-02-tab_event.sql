ALTER TABLE gathering.event
ADD COLUMN finalized BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE gathering.event
ADD COLUMN canceled BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE gathering.event
ADD CONSTRAINT event_state_check
CHECK (NOT (canceled AND finalized));


COMMENT ON COLUMN gathering.event.canceled IS
'Um evento cancelado não pode ser finalizado e todas as suas rodadas devem ser consideradas canceladas.';

COMMENT ON COLUMN gathering.event.finalized IS
'Após a finalização, não são permitidas alterações nas rodadas ou nas configurações do evento.';