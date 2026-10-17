package pt.studymatch.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CourseUnitTest {

    private static CourseUnit courseUnitWith(Map<Competency, Double> weights) {
        return new CourseUnit("PROG1", "Programming 1", 6, weights);
    }

    @Test
    void createsCourseUnitWithValidData() {
        CourseUnit unit = courseUnitWith(Map.of(Competency.PROGRAMMING, 0.8));

        assertEquals("PROG1", unit.getCode());
        assertEquals(0.8, unit.weightFor(Competency.PROGRAMMING));
    }

    @Test
    void returnsZeroForCompetencyThatDoesNotContribute() {
        CourseUnit unit = courseUnitWith(Map.of(Competency.PROGRAMMING, 0.8));

        assertEquals(0.0, unit.weightFor(Competency.DATABASES));
    }

    @Test
    void acceptsWeightsAtTheBoundaries() {
        CourseUnit unit = courseUnitWith(
                Map.of(Competency.PROGRAMMING, 0.0, Competency.DATABASES, 1.0));

        assertEquals(0.0, unit.weightFor(Competency.PROGRAMMING));
        assertEquals(1.0, unit.weightFor(Competency.DATABASES));
    }

    @Test
    void rejectsWeightAboveOne() {
        assertThrows(IllegalArgumentException.class,
                () -> courseUnitWith(Map.of(Competency.PROGRAMMING, 1.01)));
    }

    @Test
    void rejectsNegativeWeight() {
        assertThrows(IllegalArgumentException.class,
                () -> courseUnitWith(Map.of(Competency.PROGRAMMING, -0.01)));
    }

    @Test
    void rejectsNullWeightValue() {
        Map<Competency, Double> weights = new HashMap<>();
        weights.put(Competency.PROGRAMMING, null);

        assertThrows(IllegalArgumentException.class, () -> courseUnitWith(weights));
    }

    @Test
    void rejectsBlankCode() {
        assertThrows(IllegalArgumentException.class,
                () -> new CourseUnit(" ", "Programming 1", 6, Map.of()));
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new CourseUnit("PROG1", "", 6, Map.of()));
    }

    @Test
    void rejectsZeroEcts() {
        assertThrows(IllegalArgumentException.class,
                () -> new CourseUnit("PROG1", "Programming 1", 0, Map.of()));
    }

    @Test
    void doesNotChangeWhenTheOriginalWeightsMapIsModified() {
        Map<Competency, Double> weights = new HashMap<>();
        weights.put(Competency.PROGRAMMING, 0.5);
        CourseUnit unit = courseUnitWith(weights);

        weights.put(Competency.PROGRAMMING, 0.9);

        assertEquals(0.5, unit.weightFor(Competency.PROGRAMMING));
    }

    @Test
    void doesNotAllowChangingWeightsThroughTheGetter() {
        CourseUnit unit = courseUnitWith(Map.of(Competency.PROGRAMMING, 0.5));

        assertThrows(UnsupportedOperationException.class,
                () -> unit.getCompetencyWeights().put(Competency.DATABASES, 1.0));
    }
}
