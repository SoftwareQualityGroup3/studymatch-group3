package pt.studymatch.service;

import java.time.Duration;
import java.util.List;

/**
 * Constantes dos testes de nível (ADR-004).
 */
public final class TestRules {

    public static final Duration MATHEMATICS_TIME_LIMIT = Duration.ofSeconds(90);
    public static final Duration DATABASES_TIME_LIMIT = Duration.ofSeconds(60);
    public static final Duration SOFTWARE_ENGINEERING_TIME_LIMIT = Duration.ofSeconds(60);
    public static final Duration PROGRAMMING_TIME_LIMIT = Duration.ofMinutes(10);

    public static final Duration TIME_TOLERANCE = Duration.ofSeconds(2);

    public static final int CHOICE_LEVELS = 20;
    public static final int VARIANTS_PER_LEVEL = 2;

    public static final List<Integer> PROGRAMMING_WEIGHTS = List.of(1, 2, 3, 4);
    public static final int PROGRAMMING_TOTAL_WEIGHT = 10;

    public static final int MAX_SCORE = 20;
    public static final int SCORE_SCALE = 2;

    private TestRules() {
    }
}
