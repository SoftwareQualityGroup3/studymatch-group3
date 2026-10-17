package pt.studymatch.dto;

import java.math.BigDecimal;
import pt.studymatch.domain.AcademicRecord;

// dto com o que a api devolve ao cliente
// assim não expomos a entidade da base de dados nem o aluno inteiro
public record AcademicRecordResponse(
    Long id,
    String courseUnitCode,
    String courseUnitName,
    String academicYear,
    int semester,
    BigDecimal grade
) {
    // converte a entidade para dto
    // vai buscar o código e o nome da uc para o frontend não ter de fazer outro pedido
    // o ano letivo é um objeto de valor por isso só tiramos o texto com value
    public static AcademicRecordResponse from(AcademicRecord record) {
        return new AcademicRecordResponse(
            record.getId(),
            record.getCourseUnit().getCode(),
            record.getCourseUnit().getName(),
            record.getAcademicYear().value(),
            record.getSemester(),
            record.getGrade()
        );
    }
}
