package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ColdStartCalculatorTest {

    private final ColdStartCalculator calculator =
        new ColdStartCalculator();

    @Test
    void convertsSelfAssessmentToProfileScale() {
        assertEquals(
            4.0,
            calculator.convertSelfAssessment(1),
            0.001);

        assertEquals(
            20.0,
            calculator.convertSelfAssessment(5),
            0.001);
    }

    @Test
    void returnsNullWhenThereIsNoAcademicEvidenceAndNoSelfAssessment() {
        Double result =
            calculator.calculateScore(
                null,
                0,
                null);

        assertNull(result);
    }

    @Test
    void usesOnlySelfAssessmentWhenThereIsNoAcademicEvidence() {
        Double result =
            calculator.calculateScore(
                null,
                0,
                4);

        assertEquals(
            16.0,
            result,
            0.001);
    }

    @Test
    void usesOnlyAcademicScoreWhenThereIsNoSelfAssessment() {
        Double result =
            calculator.calculateScore(
                12.0,
                2,
                null);

        assertEquals(
            12.0,
            result,
            0.001);
    }

    @Test
    void combinesAcademicScoreAndSelfAssessment() {
        Double result =
            calculator.calculateScore(
                12.0,
                1,
                4);

        // academicWeight = 1 / 1.5 = 0.6667
        // final = 0.6667 * 12 + 0.3333 * 16
        assertEquals(
            13.3333,
            result,
            0.001);
    }

    @Test
    void selfAssessmentInfluenceDecreasesWithMoreEvidence() {
        Double oneEvidence =
            calculator.calculateScore(
                12.0,
                1,
                4);

        Double threeEvidence =
            calculator.calculateScore(
                12.0,
                3,
                4);

        assertEquals(
            13.3333,
            oneEvidence,
            0.001);

        assertEquals(
            12.5714,
            threeEvidence,
            0.001);
    }

    @Test
    void rejectsInvalidSelfAssessment() {
        assertThrows(
            IllegalArgumentException.class,
            () -> calculator.convertSelfAssessment(0));

        assertThrows(
            IllegalArgumentException.class,
            () -> calculator.convertSelfAssessment(6));
    }

    @Test
    void rejectsNegativeEvidenceCount() {
        assertThrows(
            IllegalArgumentException.class,
            () -> calculator.calculateScore(
                null,
                -1,
                null));
    }
}
