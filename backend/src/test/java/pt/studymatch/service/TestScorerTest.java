package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

// valores esperados calculados à mão
class TestScorerTest {

    private static List<TestScorer.ChoiceItem> items(IntPredicate correct) {
        return IntStream.rangeClosed(1, 20)
            .mapToObj(level -> new TestScorer.ChoiceItem(level, correct.test(level)))
            .toList();
    }

    @Test
    void allCorrectScoresTwenty() {
        assertEquals(new BigDecimal("20.00"), TestScorer.scoreChoice(items(level -> true)));
    }

    @Test
    void nothingCorrectScoresZero() {
        assertEquals(new BigDecimal("0.00"), TestScorer.scoreChoice(items(level -> false)));
    }

    @Test
    void higherLevelsWeighMore() {
        // níveis 1..10 somam 55: 20 × 55 / 210 = 5,238
        assertEquals(new BigDecimal("5.24"), TestScorer.scoreChoice(items(level -> level <= 10)));
        // níveis 11..20 somam 155: 20 × 155 / 210 = 14,762
        assertEquals(new BigDecimal("14.76"), TestScorer.scoreChoice(items(level -> level > 10)));
    }

    @Test
    void emptyChoiceTestIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> TestScorer.scoreChoice(List.of()));
    }

    @Test
    void programmingUsesWeightsAndPassedRatio() {
        // peso 1 com 5/10 e peso 4 completo: 20 × (0,5 + 4) / 10 = 9
        List<TestScorer.ProgrammingItem> items = List.of(
            new TestScorer.ProgrammingItem(1, 5, 10),
            new TestScorer.ProgrammingItem(2, 0, 10),
            new TestScorer.ProgrammingItem(3, 0, 10),
            new TestScorer.ProgrammingItem(4, 10, 10)
        );

        assertEquals(new BigDecimal("9.00"), TestScorer.scoreProgramming(items));
    }

    @Test
    void programmingItemWithoutTestsIsRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> new TestScorer.ProgrammingItem(1, 0, 0));
    }
}
