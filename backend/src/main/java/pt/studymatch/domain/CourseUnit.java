package pt.studymatch.domain;

import java.util.EnumMap;
import java.util.Collections;
import java.util.Map;

/**
 * Unidade curricular, com o peso que cada competência tem nela.
 * Um peso vai de 0 (não contribui) a 1 (contribuição máxima).
 */
public class CourseUnit {

    private static final double MIN_WEIGHT = 0.0;
    private static final double MAX_WEIGHT = 1.0;

    private final String code;
    private final String name;
    private final int ects;
    private final Map<Competency, Double> competencyWeights;

    public CourseUnit(String code, String name, int ects, Map<Competency, Double> competencyWeights) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Course unit code is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Course unit name is required");
        }
        if (ects <= 0) {
            throw new IllegalArgumentException("ECTS must be greater than zero");
        }
        if (competencyWeights == null) {
            throw new IllegalArgumentException("Competency weights are required");
        }

        EnumMap<Competency, Double> validated = new EnumMap<>(Competency.class);
        for (Map.Entry<Competency, Double> entry : competencyWeights.entrySet()) {
            Competency competency = entry.getKey();
            Double weight = entry.getValue();
            if (competency == null || weight == null) {
                throw new IllegalArgumentException("Competency and weight must not be null");
            }
            if (weight.isNaN() || weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
                throw new IllegalArgumentException(
                        "Weight for " + competency + " must be between 0 and 1, but was " + weight);
            }
            validated.put(competency, weight);
        }

        this.code = code.trim();
        this.name = name.trim();
        this.ects = ects;
        this.competencyWeights = Collections.unmodifiableMap(validated);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getEcts() {
        return ects;
    }

    public Map<Competency, Double> getCompetencyWeights() {
        return competencyWeights;
    }

    /** Devolve o peso da competência nesta unidade curricular, ou 0 se não contribuir. */
    public double weightFor(Competency competency) {
        return competencyWeights.getOrDefault(competency, MIN_WEIGHT);
    }
}
