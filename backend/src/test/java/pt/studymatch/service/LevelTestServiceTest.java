package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.Question;
import pt.studymatch.domain.SessionStatus;
import pt.studymatch.domain.Student;
import pt.studymatch.domain.TestAnswer;
import pt.studymatch.domain.TestSession;
import pt.studymatch.dto.AnswerRequest;
import pt.studymatch.dto.TestSessionResponse;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.exception.TestConflictException;
import pt.studymatch.exception.TestUnavailableException;
import pt.studymatch.repository.QuestionRepository;
import pt.studymatch.repository.StudentRepository;
import pt.studymatch.repository.TestAnswerRepository;
import pt.studymatch.repository.TestSessionRepository;

/**
 * Testes das regras do LevelTestService, sem base de dados:
 * os repositórios são "mocks" (imitações que nós programamos).
 */
class LevelTestServiceTest {

    private static final Long STUDENT_ID = 1L;
    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    /** Relógio falso: o tempo só avança quando o teste manda. */
    private static class FakeClock extends Clock {
        private Instant now = NOW;

        void advanceSeconds(long seconds) {
            now = now.plusSeconds(seconds);
        }

        @Override
        public Instant instant() {
            return now;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }
    }

    private final StudentRepository studentRepository = mock(StudentRepository.class);
    private final QuestionRepository questionRepository = mock(QuestionRepository.class);
    private final TestSessionRepository sessionRepository = mock(TestSessionRepository.class);
    private final TestAnswerRepository answerRepository = mock(TestAnswerRepository.class);
    private final FakeClock clock = new FakeClock();
    private final Student student = mock(Student.class);

    private final LevelTestService service = new LevelTestService(
        studentRepository, questionRepository, sessionRepository, answerRepository, clock);

    private TestSession session;
    private List<TestAnswer> items;

    // Corre antes de cada teste: o estudante 1 existe.
    @BeforeEach
    void setUp() {
        when(studentRepository.existsById(STUDENT_ID)).thenReturn(true);
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
    }

    // Banco falso: uma pergunta por nível, a resposta certa é sempre "A".
    private static List<Question> bank() {
        return IntStream.rangeClosed(1, 20)
            .mapToObj(level -> new Question("q" + level, Competency.DATABASES, level, 1,
                "Pergunta " + level, "a", "b", "c", "d", "A"))
            .toList();
    }

    // Prepara uma sessão a decorrer, com o item 1 já mostrado.
    private void givenSessionInProgress() {
        session = new TestSession(student, Competency.DATABASES, NOW);
        items = new ArrayList<>();
        List<Question> questions = bank();
        for (int i = 0; i < questions.size(); i++) {
            items.add(new TestAnswer(session, questions.get(i), i + 1));
        }
        items.get(0).start(NOW);

        when(sessionRepository.findByStudentIdAndCompetency(STUDENT_ID, Competency.DATABASES))
            .thenReturn(Optional.of(session));
        when(answerRepository.findBySessionIdOrderByPosition(any())).thenReturn(items);
    }

    private TestSessionResponse answer(int position, String option) {
        return service.answer(STUDENT_ID, Competency.DATABASES, new AnswerRequest(position, option));
    }

    @Test
    void unknownStudentIsRejected() {
        assertThrows(StudentNotFoundException.class,
            () -> service.start(99L, Competency.DATABASES));
    }

    @Test
    void startReturnsOnlyTheFirstItem() {
        when(questionRepository.findByCompetencyOrderByLevelAscVariantAsc(Competency.DATABASES))
            .thenReturn(bank());
        // O save devolve o próprio objeto que recebeu.
        when(sessionRepository.save(any(TestSession.class)))
            .thenAnswer(call -> call.getArgument(0));

        TestSessionResponse response = service.start(STUDENT_ID, Competency.DATABASES);

        assertEquals(1, response.currentItem().position());
        assertEquals(20, response.currentItem().totalItems());
        assertEquals(NOW.plusSeconds(60), response.currentItem().deadline());
    }

    @Test
    void startResumesATestInProgress() {
        givenSessionInProgress();

        TestSessionResponse response = service.start(STUDENT_ID, Competency.DATABASES);

        assertEquals(1, response.currentItem().position());
        // Não cria uma segunda sessão.
        verify(sessionRepository, never()).save(any(TestSession.class));
    }

    @Test
    void finishedTestCannotBeStartedAgain() {
        givenSessionInProgress();
        session.finish(BigDecimal.ZERO, NOW);

        assertThrows(TestConflictException.class,
            () -> service.start(STUDENT_ID, Competency.DATABASES));
    }

    @Test
    void programmingIsUnavailableAndTheAttemptIsNotUsed() {
        assertThrows(TestUnavailableException.class,
            () -> service.start(STUDENT_ID, Competency.PROGRAMMING));

        verify(sessionRepository, never()).save(any(TestSession.class));
    }

    @Test
    void incompleteBankDoesNotUseTheAttempt() {
        when(questionRepository.findByCompetencyOrderByLevelAscVariantAsc(Competency.DATABASES))
            .thenReturn(bank().subList(0, 5));

        assertThrows(TestUnavailableException.class,
            () -> service.start(STUDENT_ID, Competency.DATABASES));

        verify(sessionRepository, never()).save(any(TestSession.class));
    }

    @Test
    void answeringAnotherItemIsAConflict() {
        givenSessionInProgress();

        assertThrows(TestConflictException.class, () -> answer(2, "A"));
    }

    @Test
    void invalidOptionIsRejected() {
        givenSessionInProgress();

        assertThrows(IllegalArgumentException.class, () -> answer(1, "Z"));
    }

    @Test
    void finishedTestCannotBeAnswered() {
        givenSessionInProgress();
        session.finish(BigDecimal.ZERO, NOW);

        assertThrows(TestConflictException.class, () -> answer(1, "A"));
    }

    @Test
    void answerWithinToleranceCounts() {
        givenSessionInProgress();
        clock.advanceSeconds(62); // 60s do item + 2s de tolerância

        TestSessionResponse response = answer(1, "A");

        assertEquals("A", items.get(0).getAnswer());
        assertEquals(2, response.currentItem().position());
    }

    @Test
    void answerAfterToleranceCountsAsBlank() {
        givenSessionInProgress();
        clock.advanceSeconds(63);

        answer(1, "A");

        assertNull(items.get(0).getAnswer());
    }

    @Test
    void resumingAfterTheDeadlineSkipsTheExpiredItem() {
        givenSessionInProgress();
        clock.advanceSeconds(63);

        TestSessionResponse response = service.get(STUDENT_ID, Competency.DATABASES);

        assertEquals(2, response.currentItem().position());
        assertNull(items.get(0).getAnswer());
        // O relógio do item 2 começa agora, quando é mostrado.
        assertEquals(NOW.plusSeconds(63), items.get(1).getStartedAt());
    }

    @Test
    void lastAnswerFinishesAndScoresTheTest() {
        givenSessionInProgress();
        // Os primeiros 19 itens já estão respondidos corretamente.
        items.subList(0, 19).forEach(item -> item.finish("A", NOW));
        items.get(19).start(NOW);

        TestSessionResponse response = answer(20, "A");

        assertEquals(SessionStatus.FINISHED, response.status());
        assertEquals(new BigDecimal("20.00"), response.score());
        assertNull(response.currentItem());
    }
}
