package pt.studymatch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/** Tentativa de um estudante no teste de uma competência. */
@Entity
@Table(name = "test_session")
public class TestSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "competency", nullable = false, length = 50)
    private Competency competency;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status;

    @Column(name = "score", precision = 4, scale = 2)
    private BigDecimal score;

    protected TestSession() {
    }

    public TestSession(Student student, Competency competency, Instant startedAt) {
        this.student = student;
        this.competency = competency;
        this.startedAt = startedAt;
        this.status = SessionStatus.IN_PROGRESS;
    }

    public void finish(BigDecimal score, Instant finishedAt) {
        this.score = score;
        this.finishedAt = finishedAt;
        this.status = SessionStatus.FINISHED;
    }

    public boolean isFinished() {
        return status == SessionStatus.FINISHED;
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Competency getCompetency() {
        return competency;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public BigDecimal getScore() {
        return score;
    }
}
