CREATE TYPE onboarding_status AS ENUM (
    'INVITED',               -- Invitación enviada
    'IN_PROGRESS',           -- Proceso iniciado
    'DOCUMENTS_UPLOADED',    -- Documentos cargados
    'CONTRACT_SIGNED',       -- Contrato firmado
    'PAYMENT_CONFIGURED',    -- Pago configurado
    'PENDING_VERIFICATION',  -- Esperando revisión final
    'CORRECTION_REQUIRED',   -- El operador pidió corrección
    'ACTIVATED',             -- Cuenta activada
    'REJECTED'               -- Rechazada por fraude/riesgo
);

CREATE TYPE document_type AS ENUM (
    'IDENTITY',
    'TAX_ID',
    'PROOF_OF_ADDRESS',
    'SIGNED_CONTRACT',
    'SELFIE'
);


CREATE TYPE event_type AS ENUM (
    'STATE_CHANGE',
    'DOCUMENT_UPLOAD',
    'CORRECTION_REQUESTED',
    'APPROVAL',
    'REJECTION'
);
