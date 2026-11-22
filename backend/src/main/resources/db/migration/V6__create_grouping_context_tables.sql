CREATE TABLE grouping_context (
                                id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(150) NOT NULL,
                                course_unit_code VARCHAR(50) NULL,
                                created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                                CONSTRAINT uk_grouping_context_name UNIQUE (name),
                                CONSTRAINT fk_grouping_context_course_unit
                                  FOREIGN KEY (course_unit_code)
                                    REFERENCES course_unit(code)
);

CREATE TABLE grouping_context_student (
                                        grouping_context_id BIGINT NOT NULL,
                                        student_id BIGINT NOT NULL,

                                        PRIMARY KEY (grouping_context_id, student_id),

                                        CONSTRAINT fk_context_student_context
                                          FOREIGN KEY (grouping_context_id)
                                            REFERENCES grouping_context(id)
                                            ON DELETE CASCADE,

                                        CONSTRAINT fk_context_student_student
                                          FOREIGN KEY (student_id)
                                            REFERENCES student(id)
                                            ON DELETE CASCADE
);
