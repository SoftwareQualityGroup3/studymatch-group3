package pt.studymatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

class StudyMatchApplicationTest {

    @Test
    void mainClassIsSpringBootApplication() {
        assertTrue(StudyMatchApplication.class.isAnnotationPresent(SpringBootApplication.class));
    }
}
