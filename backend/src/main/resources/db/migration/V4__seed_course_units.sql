INSERT INTO course_unit (code, name, ects) VALUES ('PROG1', 'Programação I', 6);
INSERT INTO course_unit_competency_weight (course_unit_code, competency, weight) VALUES ('PROG1', 'PROGRAMMING', 1.0);

INSERT INTO course_unit (code, name, ects) VALUES ('ALGA', 'Álgebra Linear e Geometria Analítica', 6);
INSERT INTO course_unit_competency_weight (course_unit_code, competency, weight) VALUES ('ALGA', 'MATHEMATICS', 1.0);

INSERT INTO course_unit (code, name, ects) VALUES ('BD', 'Bases de Dados', 6);
INSERT INTO course_unit_competency_weight (course_unit_code, competency, weight) VALUES ('BD', 'DATABASES', 1.0);

INSERT INTO course_unit (code, name, ects) VALUES ('ES', 'Engenharia de Software', 6);
INSERT INTO course_unit_competency_weight (course_unit_code, competency, weight) VALUES ('ES', 'SOFTWARE_ENGINEERING', 1.0);

INSERT INTO course_unit (code, name, ects) VALUES ('WEB', 'Tecnologias Web', 6);
INSERT INTO course_unit_competency_weight (course_unit_code, competency, weight) VALUES ('WEB', 'PROGRAMMING', 0.8), ('WEB', 'SOFTWARE_ENGINEERING', 0.2);

INSERT INTO course_unit (code, name, ects) VALUES ('CALC', 'Cálculo', 6);
INSERT INTO course_unit_competency_weight (course_unit_code, competency, weight) VALUES ('CALC', 'MATHEMATICS', 1.0);
