CREATE TABLE self_assessment (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,

  -- Estudante a quem pertence a autoavaliação.
                               student_id BIGINT NOT NULL,

  -- Competência avaliada, por exemplo PROGRAMMING.
                               competency VARCHAR(50) NOT NULL,

  -- Valor da autoavaliação, entre 1 e 5.
                               value INT NOT NULL CHECK (value >= 1 AND value <= 5),

  -- Impede que o mesmo estudante tenha duas
  -- autoavaliações para a mesma competência.
                               CONSTRAINT uk_self_assessment_student_competency
                                 UNIQUE (student_id, competency),

  -- Liga a autoavaliação ao estudante.
  -- Se o estudante for apagado, as autoavaliações também são.
                               FOREIGN KEY (student_id)
                                 REFERENCES student(id)
                                 ON DELETE CASCADE
);
