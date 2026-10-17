CREATE TABLE student (
  id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  number          VARCHAR(20)  NOT NULL,
  name            VARCHAR(150) NOT NULL,
  email           VARCHAR(255) NULL,
  course          VARCHAR(100) NULL,
  curricular_year INT          NOT NULL,
  CONSTRAINT uk_student_number UNIQUE (number),
  CONSTRAINT chk_student_curricular_year CHECK (curricular_year BETWEEN 1 AND 5)
);
