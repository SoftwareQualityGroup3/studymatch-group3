package pt.studymatch.dto;

import java.math.BigDecimal;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.SessionStatus;
import pt.studymatch.domain.TestSession;

/**
 * Estado de uma sessão. currentItem é null quando o teste terminou;
 * score é null enquanto está a decorrer.
 */
public record TestSessionResponse(
    Long sessionId,
    Competency competency,
    SessionStatus status,
    QuestionResponse currentItem,
    BigDecimal score
) {

    public static TestSessionResponse from(TestSession session, QuestionResponse currentItem) {
        return new TestSessionResponse(
            session.getId(),
            session.getCompetency(),
            session.getStatus(),
            currentItem,
            session.getScore()
        );
    }
}
