package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.SelfAssessment;
import pt.studymatch.domain.Student;
import pt.studymatch.dto.SelfAssessmentRequest;
import pt.studymatch.dto.SelfAssessmentResponse;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.repository.SelfAssessmentRepository;
import pt.studymatch.repository.StudentRepository;

// testes unitários das regras do service da autoavaliação
// os repositories são mocks e assim testamos só a lógica do service
@ExtendWith(MockitoExtension.class)
class SelfAssessmentServiceTest {

    private static final Long STUDENT_ID = 1L;

    @Mock
    private SelfAssessmentRepository selfAssessmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private SelfAssessmentService selfAssessmentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student("51732", "João Xavier", "joao@upt.pt", "Engenharia Informática", 3);
    }

    // estudante novo: cria as quatro autoavaliações e devolve-as
    @Test
    void saveAllCreatesTheFourSelfAssessmentsForANewStudent() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(selfAssessmentRepository.findByStudentIdAndCompetency(eq(STUDENT_ID), any()))
            .thenReturn(Optional.empty());
        // o save devolve o próprio objeto que recebe
        when(selfAssessmentRepository.save(any(SelfAssessment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(selfAssessmentRepository.findByStudentId(STUDENT_ID)).thenReturn(List.of(
            new SelfAssessment(student, Competency.PROGRAMMING, 3),
            new SelfAssessment(student, Competency.MATHEMATICS, 4),
            new SelfAssessment(student, Competency.DATABASES, 2),
            new SelfAssessment(student, Competency.SOFTWARE_ENGINEERING, 5)));

        SelfAssessmentResponse response =
            selfAssessmentService.saveAll(STUDENT_ID, new SelfAssessmentRequest(3, 4, 2, 5));

        verify(selfAssessmentRepository, times(4)).save(any(SelfAssessment.class));
        assertEquals(new SelfAssessmentResponse(3, 4, 2, 5), response);
    }

    // já havia autoavaliação: atualiza o valor existente em vez de criar outro
    // (a unique constraint student+competência rebentava se criasse duplicados)
    @Test
    void saveAllUpdatesTheExistingValueInsteadOfCreatingADuplicate() {
        SelfAssessment existing = new SelfAssessment(student, Competency.PROGRAMMING, 2);

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(selfAssessmentRepository.findByStudentIdAndCompetency(eq(STUDENT_ID), any()))
            .thenReturn(Optional.empty());
        when(selfAssessmentRepository
            .findByStudentIdAndCompetency(STUDENT_ID, Competency.PROGRAMMING))
            .thenReturn(Optional.of(existing));
        when(selfAssessmentRepository.save(any(SelfAssessment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(selfAssessmentRepository.findByStudentId(STUDENT_ID))
            .thenReturn(List.of(existing));

        selfAssessmentService.saveAll(STUDENT_ID, new SelfAssessmentRequest(5, 4, 2, 5));

        // o objeto que já existia ficou com o valor novo
        assertEquals(5, existing.getValue());
        // continuam a ser 4 gravações (1 atualização + 3 criações), nunca 5
        verify(selfAssessmentRepository, times(4)).save(any(SelfAssessment.class));
    }

    // estudante inexistente: lança a exceção que dá 404 e não grava nada
    @Test
    void saveAllThrowsWhenTheStudentDoesNotExist() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class,
            () -> selfAssessmentService.saveAll(STUDENT_ID, new SelfAssessmentRequest(3, 4, 2, 5)));

        verify(selfAssessmentRepository, never()).save(any());
    }

    // get de um estudante com autoavaliação
    @Test
    void getByStudentIdReturnsTheStoredValues() {
        when(studentRepository.existsById(STUDENT_ID)).thenReturn(true);
        when(selfAssessmentRepository.findByStudentId(STUDENT_ID)).thenReturn(List.of(
            new SelfAssessment(student, Competency.PROGRAMMING, 3),
            new SelfAssessment(student, Competency.DATABASES, 2)));

        SelfAssessmentResponse response = selfAssessmentService.getByStudentId(STUDENT_ID);

        assertEquals(3, response.programming());
        assertEquals(2, response.databases());
        // as competências que o estudante ainda não preencheu ficam a null
        assertNull(response.mathematics());
        assertNull(response.softwareEngineering());
    }

    // get de um estudante novo: tudo a null e sem erro
    @Test
    void getByStudentIdReturnsNullsWhenThereIsNoSelfAssessment() {
        when(studentRepository.existsById(STUDENT_ID)).thenReturn(true);
        when(selfAssessmentRepository.findByStudentId(STUDENT_ID)).thenReturn(List.of());

        assertEquals(new SelfAssessmentResponse(null, null, null, null),
            selfAssessmentService.getByStudentId(STUDENT_ID));
    }

    // get de um estudante que não existe
    @Test
    void getByStudentIdThrowsWhenTheStudentDoesNotExist() {
        when(studentRepository.existsById(STUDENT_ID)).thenReturn(false);

        assertThrows(StudentNotFoundException.class,
            () -> selfAssessmentService.getByStudentId(STUDENT_ID));

        verify(selfAssessmentRepository, never()).findByStudentId(any());
    }
}
