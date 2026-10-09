-- Tabela técnica para validar a ligação backend <-> BD (não faz parte do domínio).
CREATE TABLE app_status (
    id         TINYINT      NOT NULL PRIMARY KEY,
    status     VARCHAR(20)  NOT NULL,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_status (id, status) VALUES (1, 'UP');
