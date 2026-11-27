-- Banco de perguntas (carregado a partir de question-bank/*.json)
CREATE TABLE question (
                        id             VARCHAR(60)   NOT NULL PRIMARY KEY,
                        competency     VARCHAR(50)   NOT NULL,
                        level          INT           NOT NULL,
                        variant        INT           NOT NULL,
                        statement      VARCHAR(4000) NOT NULL,
  -- as opções ficam a NULL nos problemas de Programação
                        option_a       VARCHAR(1000) NULL,
                        option_b       VARCHAR(1000) NULL,
                        option_c       VARCHAR(1000) NULL,
                        option_d       VARCHAR(1000) NULL,
                        correct_option VARCHAR(1)    NULL,

                        CONSTRAINT uk_question_competency_level_variant
                          UNIQUE (competency, level, variant)
);

-- Uma sessão por estudante e competência (tentativa única)
CREATE TABLE test_session (
                            id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                            student_id  BIGINT       NOT NULL,
                            competency  VARCHAR(50)  NOT NULL,
                            started_at  TIMESTAMP(6) NOT NULL,
                            finished_at TIMESTAMP(6) NULL,
                            status      VARCHAR(20)  NOT NULL,
                            score       DECIMAL(4,2) NULL,

                            CONSTRAINT uk_test_session_student_competency
                              UNIQUE (student_id, competency),
                            CONSTRAINT ck_test_session_status
                              CHECK (status IN ('IN_PROGRESS', 'FINISHED')),
                            CONSTRAINT fk_test_session_student
                              FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

-- Um registo por item sorteado; started_at fica a NULL até o item ser mostrado
CREATE TABLE test_answer (
                           id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                           session_id   BIGINT       NOT NULL,
                           question_id  VARCHAR(60)  NOT NULL,
                           position     INT          NOT NULL,
                           answer       VARCHAR(10)  NULL,
                           passed_tests INT          NULL,
                           total_tests  INT          NULL,
                           started_at   TIMESTAMP(6) NULL,
                           finished_at  TIMESTAMP(6) NULL,

                           CONSTRAINT uk_test_answer_session_question UNIQUE (session_id, question_id),
                           CONSTRAINT uk_test_answer_session_position UNIQUE (session_id, position),
                           CONSTRAINT fk_test_answer_session
                             FOREIGN KEY (session_id) REFERENCES test_session(id) ON DELETE CASCADE,
                           CONSTRAINT fk_test_answer_question
                             FOREIGN KEY (question_id) REFERENCES question(id)
);
