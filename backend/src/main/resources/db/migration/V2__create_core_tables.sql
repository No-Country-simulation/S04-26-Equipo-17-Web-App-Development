CREATE TABLE contractors (
    id              BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(150)  NOT NULL DEFAULT '',
    email           VARCHAR(255)  UNIQUE NOT NULL,
    country_iso     CHAR(2),
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- Operadores (incluye el usuario semilla del MVP)
CREATE TABLE operators (
    id              BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(100)  NOT NULL,
    email           VARCHAR(255)  UNIQUE NOT NULL,
    role            VARCHAR(50)   NOT NULL DEFAULT 'ADMIN_OP',
    password_hash   VARCHAR(255)  NOT NULL,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- Operador por defecto: admin@northpay.com / NorthPay123
INSERT INTO operators (full_name, email, role, password_hash)
VALUES ('Admin NorthPay', 'admin@northpay.com', 'ADMIN_OP',
        '$2a$10$...bcrypt-hash-generado...');

-- Onboardings
CREATE TABLE onboardings (
    id                BIGSERIAL PRIMARY KEY,
    contractor_id     BIGINT        NOT NULL REFERENCES contractors(id) ON DELETE CASCADE,
    current_step      INTEGER       NOT NULL DEFAULT 1 CHECK (current_step BETWEEN 1 AND 5),
    status            onboarding_status NOT NULL DEFAULT 'INVITED',
    assigned_operator_id BIGINT REFERENCES operators(id),
    invitation_token  VARCHAR(128)  UNIQUE,
    token_expires_at  TIMESTAMPTZ,
    started_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    completed_at      TIMESTAMPTZ,
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- Documentos
CREATE TABLE documents (
    id              BIGSERIAL PRIMARY KEY,
    onboarding_id   BIGINT          NOT NULL REFERENCES onboardings(id),
    doc_type        document_type   NOT NULL,
    file_url        TEXT            NOT NULL,
    status          onboarding_status NOT NULL DEFAULT 'IN_PROGRESS',
    operator_notes  TEXT,
    uploaded_at     TIMESTAMPTZ     NOT NULL DEFAULT now()
);

-- Métodos de pago
CREATE TABLE payment_methods (
    id              BIGSERIAL PRIMARY KEY,
    contractor_id   BIGINT          NOT NULL REFERENCES contractors(id),
    account_type    VARCHAR(50)     NOT NULL,
    details_json    JSONB           NOT NULL DEFAULT '{}',
    is_active       BOOLEAN         NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now()
);

-- Notificaciones
CREATE TABLE notifications (
    id              BIGSERIAL PRIMARY KEY,
    user_email      VARCHAR(255)    NOT NULL,
    message         TEXT            NOT NULL,
    is_read         BOOLEAN         NOT NULL DEFAULT false,
    sent_at         TIMESTAMPTZ     NOT NULL DEFAULT now()
);

-- Historial de eventos (auditoría)
CREATE TABLE event_history (
    id              BIGSERIAL PRIMARY KEY,
    onboarding_id   BIGINT          NOT NULL REFERENCES onboardings(id),
    operator_id     BIGINT          REFERENCES operators(id),
    event           event_type      NOT NULL,
    previous_status onboarding_status,
    new_status      onboarding_status,
    observations    TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now()
);