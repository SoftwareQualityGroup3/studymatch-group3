package pt.studymatch.dto;

import pt.studymatch.domain.Competency;
import pt.studymatch.domain.CourseUnit;
import java.util.EnumMap;
import java.util.Map;

// dto de uma uc com os pesos das competências
// cada peso vai de 0 a 1 e diz o quanto aquela uc contribui para a competência
public record CourseUnitResponse(
    String code,
    String name,
    int ects,
    // enummap tem as chaves ordenadas pela ordem do enum
    Map<Competency, Double> competencyWeights
) {
    // converte a entidade para dto
    public static CourseUnitResponse from(CourseUnit courseUnit) {
        // copia o mapa da entidade para não partilhar a mesma referência
        // a entidade guarda um mapa que não se pode alterar
        Map<Competency, Double> weights = new EnumMap<>(Competency.class);
        weights.putAll(courseUnit.getCompetencyWeights());
        return new CourseUnitResponse(
            courseUnit.getCode(),
            courseUnit.getName(),
            courseUnit.getEcts(),
            weights
        );
    }
}
