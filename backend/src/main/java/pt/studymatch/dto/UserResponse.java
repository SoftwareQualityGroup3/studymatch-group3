package pt.studymatch.dto;

import pt.studymatch.domain.AppUser;
import pt.studymatch.domain.Role;

/**
 * Dados de um utilizador devolvidos pela API (nunca inclui a password).
 */
public record UserResponse(
    Long id,
    String username,
    Role role,
    Long studentId
) {

    public static UserResponse from(AppUser user) {
        Long studentId = user.getStudent() == null ? null : user.getStudent().getId();
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), studentId);
    }
}
