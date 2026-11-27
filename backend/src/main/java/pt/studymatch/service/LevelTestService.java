package pt.studymatch.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.Question;
import pt.studymatch.domain.Student;
import pt.studymatch.domain.TestAnswer;
import pt.studymatch.domain.TestSession;
import pt.studymatch.dto.AnswerRequest;
import pt.studymatch.dto.QuestionResponse;
import pt.studymatch.dto.TestResultResponse;
import pt.studymatch.dto.TestSessionResponse;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.exception.TestConflictException;
import pt.studymatch.exception.TestSessionNotFoundException;
import pt.studymatch.exception.TestUnavailableException;
import pt.studymatch.repository.QuestionRepository;
import pt.studymatch.repository.StudentRepository;
import pt.studymatch.repository.TestAnswerRepository;
import pt.studymatch.repository.TestSessionRepository;

/**
 * Regras dos testes de nível: começar, retomar, responder e listar resultados.
 *
 * Ideia principal: o tempo é sempre medido no servidor. O cliente nunca
 * diz quanto demorou, por isso não consegue fazer batota.
 */
@Service
public class LevelTestService {

    // Respostas aceites: as 4 opções ou "Não sei".
    private static final Set<String> VALID_ANSWERS =
        Set.of("A", "B", "C", "D", TestAnswer.DONT_KNOW);

    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;
    private final TestSessionRepository sessionRepository;
    private final TestAnswerRepository answerRepository;
    private final Clock clock;

    // O Spring entrega automaticamente estes objetos (injeção de dependências).
    public LevelTestService(StudentRepository studentRepository,
                            QuestionRepository questionRepository,
                            TestSessionRepository sessionRepository,
                            TestAnswerRepository answerRepository,
                            Clock clock) {
        this.studentRepository = studentRepository;
        this.questionRepository = questionRepository;
        this.sessionRepository = sessionRepository;
        this.answerRepository = answerRepository;
        this.clock = clock;
    }

    /** Começa um teste novo, ou retoma o que está a decorrer. */
    // @Transactional: ou grava tudo, ou não grava nada.
    @Transactional
    public TestSessionResponse start(Long studentId, Competency competency) {
        // Estudante inexistente: 404.
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Verifica se já há uma sessão nesta competência.
        TestSession existing = sessionRepository
            .findByStudentIdAndCompetency(studentId, competency)
            .orElse(null);

        if (existing != null) {
            // Teste terminado não se repete: 409.
            if (existing.isFinished()) {
                throw new TestConflictException("This test is already finished");
            }
            // Ainda a decorrer: devolve-o para retomar.
            return advance(existing, answerRepository.findBySessionIdOrderByPosition(existing.getId()));
        }

        // A execução de código só chega com a S3-08.
        if (competency == Competency.PROGRAMMING) {
            throw new TestUnavailableException("The programming test is not available yet");
        }

        // Sorteia antes de gravar: se faltarem perguntas, a tentativa não é gasta.
        List<Question> questions = drawQuestions(competency);

        TestSession session = sessionRepository.save(
            new TestSession(student, competency, clock.instant()));

        // Cria um item por pergunta sorteada; as posições começam em 1.
        List<TestAnswer> items = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            items.add(new TestAnswer(session, questions.get(i), i + 1));
        }
        answerRepository.saveAll(items);

        // Mostra o primeiro item.
        return advance(session, items);
    }

    /** Estado atual da sessão, para retomar. */
    @Transactional
    public TestSessionResponse get(Long studentId, Competency competency) {
        TestSession session = findSession(studentId, competency);

        // Terminado: devolve só o resultado, sem item.
        if (session.isFinished()) {
            return TestSessionResponse.from(session, null);
        }
        return advance(session, answerRepository.findBySessionIdOrderByPosition(session.getId()));
    }

    /** Responde ao item atual. Fora de tempo conta como em branco. */
    @Transactional
    public TestSessionResponse answer(Long studentId, Competency competency, AnswerRequest request) {
        TestSession session = findSession(studentId, competency);

        // Teste terminado: 409.
        if (session.isFinished()) {
            throw new TestConflictException("This test is already finished");
        }
        // Opção que não existe: 400.
        if (!VALID_ANSWERS.contains(request.answer())) {
            throw new IllegalArgumentException("Invalid option");
        }

        List<TestAnswer> items = answerRepository.findBySessionIdOrderByPosition(session.getId());

        // Só se pode responder ao item atual (não há voltar atrás): 409.
        TestAnswer current = currentItem(items);
        if (current == null || current.getPosition() != request.position()) {
            throw new TestConflictException("This is not the current item");
        }

        // Se passou o tempo, guarda null (em branco) em vez da resposta.
        Instant now = clock.instant();
        String answer = isExpired(current, competency, now) ? null : request.answer();
        current.finish(answer, now);
        // Não é preciso chamar save: o JPA grava as alterações no fim da transação.

        return advance(session, items);
    }

    /** Lista os testes do estudante, com o resultado dos terminados. */
    @Transactional(readOnly = true) // só lê, não altera nada
    public List<TestResultResponse> results(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
        // Converte cada sessão no formato de resposta.
        return sessionRepository.findByStudentId(studentId).stream()
            .map(TestResultResponse::from)
            .toList();
    }

    // Procura a sessão; dá 404 se o estudante ou a sessão não existirem.
    private TestSession findSession(Long studentId, Competency competency) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
        return sessionRepository.findByStudentIdAndCompetency(studentId, competency)
            .orElseThrow(TestSessionNotFoundException::new);
    }

    // Fecha o item expirado, mostra o seguinte ou termina o teste.
    private TestSessionResponse advance(TestSession session, List<TestAnswer> items) {
        Instant now = clock.instant();
        TestAnswer current = currentItem(items);

        // O item que estava aberto passou do tempo: fica em branco.
        if (current != null && isExpired(current, session.getCompetency(), now)) {
            current.finish(null, now);
            current = currentItem(items);
        }

        // Não há mais itens: calcula a nota e termina.
        if (current == null) {
            session.finish(score(items), now);
            return TestSessionResponse.from(session, null);
        }

        // O relógio do item começa quando é servido pela primeira vez.
        current.start(now);

        return TestSessionResponse.from(session,
            QuestionResponse.from(current, items.size(), timeLimit(session.getCompetency())));
    }

    // O item atual é o primeiro que ainda não foi terminado.
    private TestAnswer currentItem(List<TestAnswer> items) {
        return items.stream()
            .filter(item -> !item.isFinished())
            .findFirst()
            .orElse(null);
    }

    // Passou do prazo? Prazo = início + tempo do item + 2s de tolerância.
    private boolean isExpired(TestAnswer item, Competency competency, Instant now) {
        // Item ainda não mostrado não pode ter expirado.
        if (item.getStartedAt() == null) {
            return false;
        }
        Instant limit = item.getStartedAt()
            .plus(timeLimit(competency))
            .plus(TestRules.TIME_TOLERANCE);
        return now.isAfter(limit);
    }

    // Tempo por item de cada competência (valores na classe TestRules).
    private Duration timeLimit(Competency competency) {
        return switch (competency) {
            case MATHEMATICS -> TestRules.MATHEMATICS_TIME_LIMIT;
            case DATABASES -> TestRules.DATABASES_TIME_LIMIT;
            case SOFTWARE_ENGINEERING -> TestRules.SOFTWARE_ENGINEERING_TIME_LIMIT;
            case PROGRAMMING -> TestRules.PROGRAMMING_TIME_LIMIT;
        };
    }

    // Uma variante ao acaso por nível, por ordem crescente de nível.
    private List<Question> drawQuestions(Competency competency) {
        // Agrupa as perguntas por nível; o TreeMap mantém os níveis ordenados.
        Map<Integer, List<Question>> byLevel = questionRepository
            .findByCompetencyOrderByLevelAscVariantAsc(competency)
            .stream()
            .collect(Collectors.groupingBy(Question::getLevel, TreeMap::new, Collectors.toList()));

        // Tem de haver perguntas para os 20 níveis.
        if (byLevel.size() != TestRules.CHOICE_LEVELS) {
            throw new TestUnavailableException("The question bank for this test is incomplete");
        }

        // De cada nível escolhe uma das variantes.
        return byLevel.values().stream()
            .map(variants -> variants.get(ThreadLocalRandom.current().nextInt(variants.size())))
            .toList();
    }

    // Calcula a nota (0 a 20) a partir do nível e acerto de cada item.
    private BigDecimal score(List<TestAnswer> items) {
        return TestScorer.scoreChoice(items.stream()
            .map(item -> new TestScorer.ChoiceItem(item.getQuestion().getLevel(), item.isCorrect()))
            .toList());
    }
}
