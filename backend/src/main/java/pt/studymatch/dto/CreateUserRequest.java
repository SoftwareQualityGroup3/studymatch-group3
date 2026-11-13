package pt.studymatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import pt.studymatch.domain.Role;

/**
 * Dados recebidos pela API para criar um utilizador.
 */
public record CreateUserRequest(

    @NotBlank(message = "username is required")
    @Size(max = 100, message = "username must have at most 100 characters")
    String username,

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 72, message = "password must have between 8 and 72 characters")
    String password,

    @NotNull(message = "role is required")
    Role role,

    Long studentId
) {
}
