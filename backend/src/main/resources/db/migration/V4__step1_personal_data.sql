-- HU-02: campos extendidos del Paso 1 (datos personales del contratista).
-- full_name se mantiene por compatibilidad (lo usa ContractPdfService) y se
-- autocompone en OnboardingService.updateStep1 como first_name + ' ' + last_name.

ALTER TABLE contractors
    ADD COLUMN first_name          VARCHAR(80)  NOT NULL DEFAULT '',
    ADD COLUMN last_name           VARCHAR(80)  NOT NULL DEFAULT '',
    ADD COLUMN preferred_name      VARCHAR(80),
    ADD COLUMN birth_date          DATE,
    ADD COLUMN id_document_number  VARCHAR(50),
    ADD COLUMN tax_regime          VARCHAR(80),
    ADD COLUMN phone               VARCHAR(30);
