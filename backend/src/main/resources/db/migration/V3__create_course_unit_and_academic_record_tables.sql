CREATE TABLE course_unit (
                           code VARCHAR(50) PRIMARY KEY,
                           name VARCHAR(150) NOT NULL,
                           ects INT NOT NULL CHECK (ects > 0)
);

CREATE TABLE course_unit_competency_weight (
                                             course_unit_code VARCHAR(50) NOT NULL,
                                             competency VARCHAR(50) NOT NULL,
                                             weight DOUBLE PRECISION NOT NULL CHECK (weight >= 0 AND weight <= 1),
                                             PRIMARY KEY (course_unit_code, competency),
                                             FOREIGN KEY (course_unit_code) REFERENCES course_unit(code) ON DELETE CASCADE
);

CREATE TABLE academic_record (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               student_id BIGINT NOT NULL,
                               course_unit_code VARCHAR(50) NOT NULL,
                               academic_year VARCHAR(10) NOT NULL,
                               semester INT NOT NULL CHECK (semester IN (1, 2)),
                               grade DECIMAL(4,2) NOT NULL CHECK (grade >= 0 AND grade <= 20),
                               CONSTRAINT uk_student_course_unit UNIQUE (student_id, course_unit_code),
                               FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
                               FOREIGN KEY (course_unit_code) REFERENCES course_unit(code) ON DELETE CASCADE
);
