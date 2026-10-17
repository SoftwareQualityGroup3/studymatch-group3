package pt.studymatch.domain;

import jakarta.persistence.*;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Unidade curricular, com o peso que cada competência tem nela.
 * Um peso vai de 0 (não contribui) a 1 (contribuição máxima.
 */
@Entity
@Table(name = "course_unit")
public class CourseUnit {
    private static final double MIN_WEIGHT = 0.0;
    private static final double MAX_WEIGHT = 1.0;

    @Id
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "ects", nullable = false)
    private int ects;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_unit_competency_weight", joinColumns = @JoinColumn(name = "course_unit_code"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "competency")
    @Column(name = "weight", nullable = false)
    private Map<Competency, Double> competencyWeights;

    protected CourseUnit() {
        // Exigido pelo JPA
    }

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
