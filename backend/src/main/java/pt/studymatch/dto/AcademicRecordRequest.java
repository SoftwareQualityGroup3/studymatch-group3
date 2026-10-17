package pt.studymatch.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// dto com o que o cliente envia para criar um registo
// é um record por isso é imutável e não precisa de getters
// as validações estão aqui para apanhar dados mal formados logo à entrada
public record AcademicRecordRequest(
    @NotBlank(message = "courseUnitCode is required")
    @Size(max = 50, message = "courseUnitCode must have at most 50 characters")
    String courseUnitCode,
    @NotNull(message = "grade is required")
    @DecimalMin(value = "0", message = "grade must be between 0 and 20")
    @DecimalMax(value = "20", message = "grade must be between 0 and 20")
    BigDecimal grade, // bigdecimal em vez de double para não haver erros de arredondamento

    // o regex deixa passar espaços nas pontas porque o domínio faz trim depois
    // a regra de o segundo ano ser o seguinte só é verificada no domínio
    @NotBlank(message = "academicYear is required")
    @Pattern(regexp = "\\s*\\d{4}/\\d{2}\\s*",
        message = "academicYear must have the format YYYY/YY (e.g. 2024/25)")
    String academicYear,

    // semestre só pode ser 1 ou 2
    // integer e não int para o notnull conseguir detetar quando falta
    @NotNull(message = "semester is required")
    @Min(value = 1, message = "semester must be 1 or 2")
    @Max(value = 2, message = "semester must be 1 or 2")
    Integer semester
) {
}
