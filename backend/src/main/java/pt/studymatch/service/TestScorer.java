package pt.studymatch.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * Cálculo puro do resultado (0 a 20) de um teste de nível.
 * Respostas erradas ou em branco valem 0; não há desconto.
 */
public final class TestScorer {

    /** Item de escolha múltipla: nível e se foi acertado. */
    public record ChoiceItem(int level, boolean correct) {
    }

    /** Problema de Programação: peso e testes passados/total. */
    public record ProgrammingItem(int weight, int passed, int total) {
        public ProgrammingItem {
            if (total <= 0 || passed < 0 || passed > total) {
                throw new IllegalArgumentException("Invalid test counts");
            }
        }
    }

    private TestScorer() {
    }

    /** 20 × Σ(nível × acerto) / Σ nível */
    public static BigDecimal scoreChoice(List<ChoiceItem> items) {
        int totalLevels = items.stream().mapToInt(ChoiceItem::level).sum();
        if (totalLevels <= 0) {
            throw new IllegalArgumentException("A test needs at least one item");
        }

        int correctLevels = items.stream()
            .filter(ChoiceItem::correct)
            .mapToInt(ChoiceItem::level)
            .sum();

        return BigDecimal.valueOf((long) TestRules.MAX_SCORE * correctLevels)
            .divide(BigDecimal.valueOf(totalLevels), TestRules.SCORE_SCALE, RoundingMode.HALF_UP);
    }

    /** 20 × Σ(peso × passados/total) / 10 */
    public static BigDecimal scoreProgramming(List<ProgrammingItem> items) {
        BigDecimal weighted = BigDecimal.ZERO;

        for (ProgrammingItem item : items) {
            BigDecimal part = BigDecimal.valueOf((long) item.weight() * item.passed())
                .divide(BigDecimal.valueOf(item.total()), MathContext.DECIMAL64);
            weighted = weighted.add(part);
        }

        return weighted
            .multiply(BigDecimal.valueOf(TestRules.MAX_SCORE))
            .divide(
                BigDecimal.valueOf(TestRules.PROGRAMMING_TOTAL_WEIGHT),
                TestRules.SCORE_SCALE,
                RoundingMode.HALF_UP
            );
    }
}
