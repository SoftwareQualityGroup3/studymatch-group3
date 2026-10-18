package pt.studymatch.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SelfAssessmentTest {

    private final Student student =
        new Student("51732", "Joao Xavier", "joao@example.com", "Engenharia Informatica", 3);

    @ParameterizedTest(name = "aceita autoavaliação {0}")
    @ValueSource(ints = {1, 5})
    void aceitaAutoavaliacaoNosLimites(int value) {
        SelfAssessment assessment = assertDoesNotThrow(() ->
            new SelfAssessment(student, Competency.PROGRAMMING, value));

        assertEquals(value, assessment.getValue());
    }

    @ParameterizedTest(name = "rejeita autoavaliação {0}")
    @ValueSource(ints = {0, 6})
    void rejeitaAutoavaliacaoForaDoIntervalo(int value) {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new SelfAssessment(student, Competency.PROGRAMMING, value)
        );

        assertEquals("Self-assessment value must be between 1 and 5", exception.getMessage());
    }
}
