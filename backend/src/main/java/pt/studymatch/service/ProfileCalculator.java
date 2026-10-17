package pt.studymatch.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import pt.studymatch.domain.AcademicRecord;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.ProfileSource;
import pt.studymatch.domain.StudentProfile;
import pt.studymatch.domain.StudentProfile.CompetencyResult;

public class ProfileCalculator {

    /**
     * Calcula o perfil académico de raiz a partir dos resultados fornecidos.
     *
     * <p>Cada chamada recalcula todas as pontuações usando apenas a lista
     * recebida.</p>
     *
     * @param records resultados académicos usados no cálculo
     * @return perfil académico calculado
     */
    public StudentProfile calculate(List<AcademicRecord> records) {
        Map<Competency, CompetencyResult> profiles =
            new EnumMap<>(Competency.class);

        for (Competency competency : Competency.values()) {
            profiles.put(
                competency,
                calculateForCompetency(records, competency));
        }

        return new StudentProfile(profiles);
    }

    private CompetencyResult calculateForCompetency(
        List<AcademicRecord> records,
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

        if (totalWeight == 0.0) {
            return new CompetencyResult(
                null,
                0,
                ProfileSource.ACADEMIC);
        }

        double score = totalWeightedGrade / totalWeight;

        return new CompetencyResult(
            score,
            evidenceCount,
            ProfileSource.ACADEMIC);
    }
}
