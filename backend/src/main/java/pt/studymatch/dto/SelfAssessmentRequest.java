package pt.studymatch.dto;

import java.util.EnumMap;
import java.util.Map;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import pt.studymatch.domain.Competency;

// dto com a autoavaliação que o estudante envia (uma nota de 1 a 5 por competência)
// são Integer e não int para o notnull conseguir detetar quando falta um valor
// valores fora de 1-5 ou em falta dão 400 sem chegar ao service
public record SelfAssessmentRequest(
    @NotNull(message = "programming is required")
    @Min(value = 1, message = "programming must be between 1 and 5")
    @Max(value = 5, message = "programming must be between 1 and 5")
    Integer programming,

    @NotNull(message = "mathematics is required")
    @Min(value = 1, message = "mathematics must be between 1 and 5")
    @Max(value = 5, message = "mathematics must be between 1 and 5")
    Integer mathematics,

    @NotNull(message = "databases is required")
    @Min(value = 1, message = "databases must be between 1 and 5")
    @Max(value = 5, message = "databases must be between 1 and 5")
    Integer databases,

    @NotNull(message = "softwareEngineering is required")
    @Min(value = 1, message = "softwareEngineering must be between 1 and 5")
    @Max(value = 5, message = "softwareEngineering must be between 1 and 5")
    Integer softwareEngineering
) {

    // passa os quatro valores para o formato que o domínio usa (competência - valor)
    public Map<Competency, Integer> toValues() {
        Map<Competency, Integer> values = new EnumMap<>(Competency.class);
        values.put(Competency.PROGRAMMING, programming);
        values.put(Competency.MATHEMATICS, mathematics);
        values.put(Competency.DATABASES, databases);
        values.put(Competency.SOFTWARE_ENGINEERING, softwareEngineering);
        return values;
    }
}
