package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.studymatch.domain.AcademicRecord;
import pt.studymatch.domain.AcademicYear;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.CourseUnit;
import pt.studymatch.domain.Student;
import pt.studymatch.dto.AcademicRecordRequest;
import pt.studymatch.dto.AcademicRecordResponse;
import pt.studymatch.exception.CourseUnitNotFoundException;
import pt.studymatch.exception.DuplicateAcademicRecordException;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.repository.AcademicRecordRepository;
import pt.studymatch.repository.CourseUnitRepository;
import pt.studymatch.repository.StudentRepository;

// testes unitários das regras do service dos registos
// os três repositories são mocks e assim testamos só a lógica do service
@ExtendWith(MockitoExtension.class)
class AcademicRecordServiceTest {
    private static final Long STUDENT_ID = 1L;
    @Mock
    private AcademicRecordRepository academicRecordRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseUnitRepository courseUnitRepository;
    @InjectMocks
    private AcademicRecordService academicRecordService;
    // dados de exemplo criados de novo antes de cada teste
    private Student student;
    private CourseUnit courseUnit;

    // corre antes de cada teste para eles não dependerem uns dos outros
    @BeforeEach
    void setUp() {
        student = new Student("51732", "João Xavier", "joao@upt.pt", "Engenharia Informática", 3);
        courseUnit = new CourseUnit("PROG1", "Programação I", 6, Map.of(Competency.PROGRAMMING, 1.0));
    }

    // atalho para criar pedidos sem repetir código
    private static AcademicRecordRequest request(String code, String grade, String year, Integer semester) {
        return new AcademicRecordRequest(code, new BigDecimal(grade), year, semester);
    }

    // configura os mocks para o caso em que o aluno e a uc existem
    // evita repetir isto em todos os testes
    private void studentAndCourseUnitExist() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(courseUnitRepository.findById("PROG1")).thenReturn(Optional.of(courseUnit));
    }

    // caminho feliz
    // o save é simulado para devolver o próprio objeto que recebe
    // a nota 15.5 fica guardada como 15.50 por causa do setscale do domínio
    @Test
    void createsRecordForExistingStudentAndCourseUnit() {
        studentAndCourseUnitExist();
        when(academicRecordRepository.existsByStudent_IdAndCourseUnit_Code(STUDENT_ID, "PROG1"))
            .thenReturn(false);
        when(academicRecordRepository.save(any(AcademicRecord.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        AcademicRecordResponse response =
            academicRecordService.create(STUDENT_ID, request("PROG1", "15.5", "2024/25", 1));
        assertEquals("PROG1", response.courseUnitCode());
        assertEquals("Programação I", response.courseUnitName());
        assertEquals("2024/25", response.academicYear());
        assertEquals(1, response.semester());
        assertEquals(new BigDecimal("15.50"), response.grade());
    }

    // o código com espaços tem de ser limpo antes de ir procurar a uc
    @Test
    void trimsCourseUnitCodeBeforeLookingItUp() {
        studentAndCourseUnitExist();
        when(academicRecordRepository.save(any(AcademicRecord.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        AcademicRecordResponse response =
            academicRecordService.create(STUDENT_ID, request(" PROG1 ", "12", "2024/25", 2));
        assertEquals("PROG1", response.courseUnitCode());
    }

    // testes dos limites da nota
    // o 0 é aceite
    @Test
    void acceptsGradesAtTheBoundaries() {
        studentAndCourseUnitExist();
        when(academicRecordRepository.save(any(AcademicRecord.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        assertEquals(new BigDecimal("0.00"),
            academicRecordService.create(STUDENT_ID, request("PROG1", "0", "2024/25", 1)).grade());
    }

    // o 20 também é aceite
    @Test
    void acceptsMaximumGrade() {
        studentAndCourseUnitExist();
        when(academicRecordRepository.save(any(AcademicRecord.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        assertEquals(new BigDecimal("20.00"),
            academicRecordService.create(STUDENT_ID, request("PROG1", "20", "2024/25", 1)).grade());
    }

    // aluno inexistente lança exceção e nunca chega a guardar nada
    @Test
    void throwsWhenStudentDoesNotExist() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class,
            () -> academicRecordService.create(STUDENT_ID, request("PROG1", "15", "2024/25", 1)));
        verify(academicRecordRepository, never()).save(any());
    }

    // uc inexistente lança exceção e também não guarda nada
    @Test
    void throwsWhenCourseUnitDoesNotExist() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(courseUnitRepository.findById("XPTO")).thenReturn(Optional.empty());
        assertThrows(CourseUnitNotFoundException.class,
            () -> academicRecordService.create(STUDENT_ID, request("XPTO", "15", "2024/25", 1)));
        verify(academicRecordRepository, never()).save(any());
    }

    // duplicado lança exceção e o verify never garante que o save não foi chamado
    @Test
    void throwsWhenStudentAlreadyHasRecordForCourseUnit() {
        studentAndCourseUnitExist();
        when(academicRecordRepository.existsByStudent_IdAndCourseUnit_Code(STUDENT_ID, "PROG1"))
            .thenReturn(true);
        assertThrows(DuplicateAcademicRecordException.class,
            () -> academicRecordService.create(STUDENT_ID, request("PROG1", "15", "2024/25", 1)));
        verify(academicRecordRepository, never()).save(any());
    }

    // a seguir ficam as regras do domínio
    // 20.01 já é inválido
    @Test
    void rejectsGradeAboveTwenty() {
        studentAndCourseUnitExist();
        assertThrows(IllegalArgumentException.class,
            () -> academicRecordService.create(STUDENT_ID, request("PROG1", "20.01", "2024/25", 1)));
        verify(academicRecordRepository, never()).save(any());
    }

    // -0.01 é inválido
    @Test
    void rejectsNegativeGrade() {
        studentAndCourseUnitExist();
        assertThrows(IllegalArgumentException.class,
            () -> academicRecordService.create(STUDENT_ID, request("PROG1", "-0.01", "2024/25", 1)));
    }

    // semestre 3 não existe
    @Test
    void rejectsInvalidSemester() {
        studentAndCourseUnitExist();
        assertThrows(IllegalArgumentException.class,
            () -> academicRecordService.create(STUDENT_ID, request("PROG1", "15", "2024/25", 3)));
    }

    // 2024/30 tem o formato certo mas os anos não são seguidos
    @Test
    void rejectsInconsistentAcademicYear() {
        studentAndCourseUnitExist();
        assertThrows(IllegalArgumentException.class,
            () -> academicRecordService.create(STUDENT_ID, request("PROG1", "15", "2024/30", 1)));
    }

    // listagem com o nome da uc preenchido e a nota com duas casas decimais
    @Test
    void listsRecordsOfExistingStudentWithCourseUnitName() {
        AcademicRecord record = new AcademicRecord(
            student, courseUnit, new AcademicYear("2024/25"), 1, new BigDecimal("14"));
        when(studentRepository.existsById(STUDENT_ID)).thenReturn(true);
        when(academicRecordRepository.findByStudent_Id(STUDENT_ID)).thenReturn(List.of(record));
        List<AcademicRecordResponse> responses = academicRecordService.findByStudentId(STUDENT_ID);
        assertEquals(1, responses.size());
        assertEquals("Programação I", responses.getFirst().courseUnitName());
        assertEquals(new BigDecimal("14.00"), responses.getFirst().grade());
    }

    // aluno sem registos tem lista vazia e não é erro
    @Test
    void returnsEmptyListWhenStudentHasNoRecords() {
        when(studentRepository.existsById(STUDENT_ID)).thenReturn(true);
        when(academicRecordRepository.findByStudent_Id(STUDENT_ID)).thenReturn(List.of());
        assertEquals(0, academicRecordService.findByStudentId(STUDENT_ID).size());
    }

    // listar de um aluno que não existe lança exceção
    @Test
    void throwsWhenListingRecordsOfUnknownStudent() {
        when(studentRepository.existsById(STUDENT_ID)).thenReturn(false);
        assertThrows(StudentNotFoundException.class,
            () -> academicRecordService.findByStudentId(STUDENT_ID));
    }
}
