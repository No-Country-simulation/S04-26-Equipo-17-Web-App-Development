-- HU-02 / HU-03: storage_key explícita para borrar/reemplazar archivos en el bucket
-- sin depender de parsear file_url. Aplica a TODOS los uploads (IDENTITY, TAX_ID,
-- PROOF_OF_ADDRESS, SIGNED_CONTRACT, SELFIE).
-- Nullable porque filas previas (si las hubiera) no la tienen.

ALTER TABLE documents
    ADD COLUMN storage_key VARCHAR(255);
