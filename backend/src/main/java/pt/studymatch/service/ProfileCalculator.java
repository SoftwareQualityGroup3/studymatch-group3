package pt.studymatch.service;

import pt.studymatch.domain.ProfileSource;
import pt.studymatch.domain.StudentProfile;

import java.util.List;

public class ProfileCalculator {

    /**
     * Calcula o perfil académico de raiz a partir dos resultados fornecidos.
     *
     * <p>Cada chamada recalcula todas as pontuações usando apenas a lista
     * recebida. O calculator não guarda nem reutiliza resultados de chamadas
     * anteriores.</p>
     *
     * @param records resultados académicos usados no cálculo
     * @return perfil académico calculado
     * @throws IllegalArgumentException se existir uma nota ou peso inválido
     */
    public StudentProfile calculate(List<AcademicRecord> records) {
        double totalWeightedGrade = 0.0;
        double totalWeight = 0.0;
        int evidenceCount = 0;

        for (AcademicRecord record : records) {
            if (record.getGrade() < 0 || record.getGrade() > 20) {
                throw new IllegalArgumentException(
                    "Grade must be between 0 and 20");
            }

            double weight = record.getWeight();

            if (weight < 0 || weight > 1) {
                throw new IllegalArgumentException(
                    "Weight must be between 0 and 1");
            }

            if (weight > 0) {
                totalWeightedGrade += record.getGrade() * weight;
                totalWeight += weight;
                evidenceCount++;
            }
        }

        if (totalWeight == 0.0) {
            return new StudentProfile(
                null,
                0,
                ProfileSource.ACADEMIC);
        }

        double score = totalWeightedGrade / totalWeight;

        return new StudentProfile(
            score,
            evidenceCount,
            ProfileSource.ACADEMIC);
    }
}
