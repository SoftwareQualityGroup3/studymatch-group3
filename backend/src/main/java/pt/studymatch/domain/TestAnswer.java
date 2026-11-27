package pt.studymatch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Item sorteado numa sessão.
 * answer a null depois de terminado significa resposta em branco.
 */
@Entity
@Table(name = "test_answer")
public class TestAnswer {

    /** Valor guardado quando o estudante escolhe "Não sei". */
    public static final String DONT_KNOW = "DONT_KNOW";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TestSession session;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "answer", length = 10)
    private String answer;

    @Column(name = "passed_tests")
    private Integer passedTests;

    @Column(name = "total_tests")
    private Integer totalTests;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    protected TestAnswer() {
    }

    public TestAnswer(TestSession session, Question question, int position) {
        this.session = session;
        this.question = question;
        this.position = position;
    }

    /** Marca o momento em que o item foi mostrado; só conta a primeira vez. */
    public void start(Instant now) {
        if (startedAt == null) {
            startedAt = now;
        }
    }

    /** Fecha o item. Um answer a null conta como em branco. */
    public void finish(String answer, Instant now) {
        this.answer = answer;
        this.finishedAt = now;
    }

    public boolean isFinished() {
        return finishedAt != null;
    }

    public boolean isCorrect() {
        return answer != null && answer.equals(question.getCorrectOption());
    }

    public Long getId() {
        return id;
    }

    public TestSession getSession() {
        return session;
    }

    public Question getQuestion() {
        return question;
    }

    public int getPosition() {
        return position;
    }

    public String getAnswer() {
        return answer;
    }

    public Integer getPassedTests() {
        return passedTests;
    }

    public Integer getTotalTests() {
        return totalTests;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }
}
