CREATE TABLE app_user (
                        id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        username      VARCHAR(100) NOT NULL,
                        password_hash VARCHAR(100) NOT NULL,
                        role          VARCHAR(30)  NOT NULL,
                        student_id    BIGINT       NULL,
                        CONSTRAINT uk_app_user_username UNIQUE (username),
                        CONSTRAINT uk_app_user_student UNIQUE (student_id),
                        CONSTRAINT chk_app_user_role CHECK (role IN ('ESTUDANTE', 'DOCENTE', 'SERVICOS_ACADEMICOS')),
                        CONSTRAINT fk_app_user_student FOREIGN KEY (student_id) REFERENCES student (id)
);
