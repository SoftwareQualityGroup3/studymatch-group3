package pt.studymatch.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import pt.studymatch.domain.AcademicRecord;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.ProfileSource;
import pt.studymatch.domain.SelfAssessment;
import pt.studymatch.domain.StudentProfile;
import pt.studymatch.domain.StudentProfile.CompetencyResult;

@Component
public class ProfileCalculator {

    // Responsável por aplicar a regra de cold start.
    private final ColdStartCalculator coldStartCalculator =
        new ColdStartCalculator();

    /**
     * Calcula o perfil académico de raiz a partir dos resultados fornecidos.
     *
     * <p>Cada chamada recalcula todas as pontuações usando apenas a lista
     * recebida.</p>
     *
     * Mantém o comportamento original do S2-07 quando não existem
     * autoavaliações.
     *
     * @param records resultados académicos usados no cálculo
     * @return perfil académico calculado
     */
    public StudentProfile calculate(List<AcademicRecord> records) {
        return calculate(records, List.of());
    }

    /**
     * Calcula o perfil usando resultados académicos e autoavaliações.
     */
    public StudentProfile calculate(
        List<AcademicRecord> records,
        List<SelfAssessment> selfAssessments) {

        Map<Competency, CompetencyResult> profiles =
            new EnumMap<>(Competency.class);

        for (Competency competency : Competency.values()) {
            profiles.put(
                competency,
                calculateForCompetency(
                    records,
                    selfAssessments,
                    competency));
        }

        return new StudentProfile(profiles);
    }

    private CompetencyResult calculateForCompetency(
        List<AcademicRecord> records,
        List<SelfAssessment> selfAssessments,
        Competency competency) {

        double totalWeightedGrade = 0.0;
        double totalWeight = 0.0;
        int evidenceCount = 0;

        for (AcademicRecord record : records) {
            double grade = record.getGrade().doubleValue();

            if (grade < 0 || grade > 20) {
                throw new IllegalArgumentException(
                    "Grade must be between 0 and 20");
            }

            if (record.getCourseUnit().getCompetencyWeights().isEmpty()) {
                throw new IllegalArgumentException(
                    "Course unit must have at least one competency weight");
            }

            // O peso está na CourseUnit e depende da competência.
            double weight =
                record.getCourseUnit().weightFor(competency);

            if (weight < 0 || weight > 1) {
                throw new IllegalArgumentException(
                    "Weight must be between 0 and 1");
            }

            if (weight > 0) {
                totalWeightedGrade += grade * weight;
                totalWeight += weight;
                evidenceCount++;
            }
        }

        // Calcula a pontuação académica.
        // Se não houver evidências, fica null.
        Double academicScore = null;

        if (totalWeight > 0.0) {
            academicScore =
                totalWeightedGrade / totalWeight;
        }

        // Procura uma autoavaliação para esta competência.
        Integer selfAssessmentValue = null;

        for (SelfAssessment selfAssessment : selfAssessments) {
            if (selfAssessment.getCompetency() == competency) {
                selfAssessmentValue =
                    selfAssessment.getValue();
                break;
            }
        }

        // Aplica as regras de cold start.
        Double finalScore =
            coldStartCalculator.calculateScore(
                academicScore,
                evidenceCount,
                selfAssessmentValue);

        // Define a origem da pontuação.
        ProfileSource source;

        if (evidenceCount == 0
            && selfAssessmentValue != null) {

            source = ProfileSource.SELF_ASSESSMENT;

        } else if (evidenceCount > 0
            && selfAssessmentValue != null) {

            source = ProfileSource.MIXED;

        } else {

            // Sem autoavaliação mantém o comportamento do S2-07.
            source = ProfileSource.ACADEMIC;
        }

        return new CompetencyResult(
            finalScore,
            evidenceCount,
            source);
    }
}
