package pt.studymatch.dto;

import java.math.BigDecimal;
import java.time.Instant;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.SessionStatus;
import pt.studymatch.domain.TestSession;

/** Resultado de um teste, para a listagem do estudante. */
public record TestResultResponse(
    Competency competency,
    SessionStatus status,
    Instant startedAt,
    Instant finishedAt,
    BigDecimal score
) {

    public static TestResultResponse from(TestSession session) {
        return new TestResultResponse(
            session.getCompetency(),
            session.getStatus(),
            session.getStartedAt(),
            session.getFinishedAt(),
            session.getScore()
        );
    }
}
