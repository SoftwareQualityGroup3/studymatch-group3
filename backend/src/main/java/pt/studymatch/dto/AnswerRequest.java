package pt.studymatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resposta ao item atual: A, B, C, D ou DONT_KNOW ("Não sei").
 * position identifica o item a que se está a responder.
 */
public record AnswerRequest(
    @NotNull Integer position,
    @NotBlank String answer
) {
}
