package pt.studymatch.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AcademicRecordTest {

    private CourseUnit createValidCourseUnit() {
        return new CourseUnit("MAT101", "Matemática I", 6, Map.of(Competency.PROGRAMMING, 0.5));
    }

    @Test
    void criaAcademicRecordValido() {
        Student student = new Student("12345", "João Silva", "joao@email.com", "Engenharia Informática", 2);
        CourseUnit courseUnit = createValidCourseUnit();
        AcademicYear academicYear = new AcademicYear("2024/25");

        AcademicRecord record = new AcademicRecord(student, courseUnit, academicYear, 1, new BigDecimal("15.50"));

        assertEquals(student, record.getStudent());
        assertEquals(courseUnit, record.getCourseUnit());
        assertEquals("2024/25", record.getAcademicYear().toString());
        assertEquals(1, record.getSemester());
        assertEquals(0, new BigDecimal("15.50").compareTo(record.getGrade()));
    }

    @Test
    void aceitaNotaNoLimiteInferiorZero() {
        Student student = new Student("12345", "João Silva", null, null, 1);
        CourseUnit courseUnit = createValidCourseUnit();
        AcademicYear academicYear = new AcademicYear("2024/25");

        AcademicRecord record = new AcademicRecord(student, courseUnit, academicYear, 2, BigDecimal.ZERO);
        assertEquals(0, BigDecimal.ZERO.compareTo(record.getGrade()));
    }

    @Test
    void aceitaNotaNoLimiteSuperiorVinte() {
        Student student = new Student("12345", "João Silva", null, null, 1);
        CourseUnit courseUnit = createValidCourseUnit();
        AcademicYear academicYear = new AcademicYear("2024/25");

        AcademicRecord record = new AcademicRecord(student, courseUnit, academicYear, 2, BigDecimal.valueOf(20));
        assertEquals(0, BigDecimal.valueOf(20).compareTo(record.getGrade()));
    }

    @Test
    void rejeitaNotaNegativa() {
        Student student = new Student("12345", "João Silva", null, null, 1);
        CourseUnit courseUnit = createValidCourseUnit();
        AcademicYear academicYear = new AcademicYear("2024/25");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new AcademicRecord(student, courseUnit, academicYear, 1, new BigDecimal("-0.01"))
        );
        assertEquals("Grade must be between 0 and 20", exception.getMessage());
    }

    @Test
    void rejeitaNotaAcimaDeVinte() {
        Student student = new Student("12345", "João Silva", null, null, 1);
        CourseUnit courseUnit = createValidCourseUnit();
        AcademicYear academicYear = new AcademicYear("2024/25");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new AcademicRecord(student, courseUnit, academicYear, 1, new BigDecimal("20.01"))
        );
        assertEquals("Grade must be between 0 and 20", exception.getMessage());
    }

    @Test
    void rejeitaSemestreInvalido() {
        Student student = new Student("12345", "João Silva", null, null, 1);
        CourseUnit courseUnit = createValidCourseUnit();
        AcademicYear academicYear = new AcademicYear("2024/25");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new AcademicRecord(student, courseUnit, academicYear, 3, new BigDecimal("14.00"))
        );
        assertEquals("Semester must be 1 or 2", exception.getMessage());
    }

    @Test
    void validaAssociacaoEstudanteEUnidadeCurricularUnica() {
        // Garante que a entidade aceita corretamente os atributos de associação únicos
        // (A restrição de unicidade por estudante e unidade curricular está mapeada a nível de base de dados via @UniqueConstraint)
        Student student = new Student("12345", "João Silva", null, null, 1);
        CourseUnit courseUnit = createValidCourseUnit();
        AcademicYear academicYear = new AcademicYear("2024/25");

        AcademicRecord record = new AcademicRecord(student, courseUnit, academicYear, 1, new BigDecimal("16.00"));

        assertNotNull(record.getStudent());
        assertNotNull(record.getCourseUnit());
        assertEquals("MAT101", record.getCourseUnit().getCode());
        assertEquals("12345", record.getStudent().getNumber());
    }
}
