package pt.studymatch.dto;

import pt.studymatch.domain.Student;

/**
 * Dados devolvidos pela API.
 *
 * Serve para não devolver diretamente a entidade Student.
 */
public record StudentResponse(
    Long id,
    String number,
    String name,
    String email,
    String course,
    int curricularYear
) {

    /**
     * Converte uma entidade Student num DTO de resposta.
     */
    public static StudentResponse from(Student student) {
        return new StudentResponse(
            student.getId(),
            student.getNumber(),
            student.getName(),
            student.getEmail(),
            student.getCourse(),
            student.getCurricularYear()
        );
    }
}
