package pt.studymatch.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados recebidos pela API para criar ou atualizar um estudante.
 */
public record StudentRequest(

    @NotBlank(message = "number is required")
    @Size(max = 20, message = "number must have at most 20 characters")
    String number,

    @NotBlank(message = "name is required")
    @Size(max = 150, message = "name must have at most 150 characters")
    String name,

    @Size(max = 255, message = "email must have at most 255 characters")
    String email,

    @Size(max = 100, message = "course must have at most 100 characters")
    String course,

    @Min(value = 1, message = "curricularYear must be between 1 and 5")
    @Max(value = 5, message = "curricularYear must be between 1 and 5")
    int curricularYear
) {
}
