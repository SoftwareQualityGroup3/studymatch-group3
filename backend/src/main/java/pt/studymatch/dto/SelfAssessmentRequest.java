package pt.studymatch.dto;

import java.util.EnumMap;
import java.util.Map;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import pt.studymatch.domain.Competency;

// dto com a autoavaliação que o estudante envia (uma nota de 1 a 5 por competência)
// o estudante pode preencher só as competências que quiser, por isso cada campo é opcional
// são Integer e não int para poderem ficar a null quando a competência não é enviada
// valores fora de 1-5 dão 400 sem chegar ao service (o null passa no min e no max)
// pedido sem nenhuma competência é rejeitado no service
public record SelfAssessmentRequest(
    @Min(value = 1, message = "programming must be between 1 and 5")
    @Max(value = 5, message = "programming must be between 1 and 5")
    Integer programming,

    @Min(value = 1, message = "mathematics must be between 1 and 5")
    @Max(value = 5, message = "mathematics must be between 1 and 5")
    Integer mathematics,

    @Min(value = 1, message = "databases must be between 1 and 5")
    @Max(value = 5, message = "databases must be between 1 and 5")
    Integer databases,

    @Min(value = 1, message = "softwareEngineering must be between 1 and 5")
    @Max(value = 5, message = "softwareEngineering must be between 1 and 5")
    Integer softwareEngineering
) {

    // passa os valores enviados para o formato que o domínio usa (competência - valor)
    // as competências que não foram enviadas (null) ficam de fora
    public Map<Competency, Integer> toValues() {
        Map<Competency, Integer> values = new EnumMap<>(Competency.class);
        putIfPresent(values, Competency.PROGRAMMING, programming);
        putIfPresent(values, Competency.MATHEMATICS, mathematics);
        putIfPresent(values, Competency.DATABASES, databases);
        putIfPresent(values, Competency.SOFTWARE_ENGINEERING, softwareEngineering);
        return values;
    }

    private static void putIfPresent(
        Map<Competency, Integer> values, Competency competency, Integer value) {

        if (value != null) {
            values.put(competency, value);
        }
    }
}
