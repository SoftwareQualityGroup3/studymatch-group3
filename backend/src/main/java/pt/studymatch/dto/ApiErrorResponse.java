package pt.studymatch.dto;

import java.util.Map;

/**
 * Formato comum das respostas de erro da API.
 */
public record ApiErrorResponse(
    Map<String, String> fieldErrors
) {
}
