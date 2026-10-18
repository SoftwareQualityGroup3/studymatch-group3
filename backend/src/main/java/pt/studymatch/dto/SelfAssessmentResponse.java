package pt.studymatch.dto;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.SelfAssessment;

// dto com a autoavaliação que a api devolve
// um estudante que ainda não preencheu nada recebe os quatro valores a null (e não um 404)
// assim o frontend distingue "ainda sem autoavaliação" de "estudante não existe"
public record SelfAssessmentResponse(
    Integer programming,
    Integer mathematics,
    Integer databases,
    Integer softwareEngineering
) {

    // converte as autoavaliações guardadas (uma por competência) para o dto
    public static SelfAssessmentResponse from(List<SelfAssessment> selfAssessments) {
        Map<Competency, Integer> values = new EnumMap<>(Competency.class);

        for (SelfAssessment selfAssessment : selfAssessments) {
            values.put(selfAssessment.getCompetency(), selfAssessment.getValue());
        }

        return new SelfAssessmentResponse(
            values.get(Competency.PROGRAMMING),
            values.get(Competency.MATHEMATICS),
            values.get(Competency.DATABASES),
            values.get(Competency.SOFTWARE_ENGINEERING)
        );
    }
}
