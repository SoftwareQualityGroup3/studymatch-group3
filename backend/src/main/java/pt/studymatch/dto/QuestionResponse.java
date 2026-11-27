package pt.studymatch.dto;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import pt.studymatch.domain.Question;
import pt.studymatch.domain.TestAnswer;

/** Item atual do teste. Nunca inclui a resposta correta. */
public record QuestionResponse(
    int position,
    int totalItems,
    String statement,
    List<Option> options,
    long timeLimitSeconds,
    Instant deadline
) {

    public record Option(String id, String text) {
    }

    public static QuestionResponse from(TestAnswer item, int totalItems, Duration timeLimit) {
        Question question = item.getQuestion();

        // Os problemas de Programação não têm opções.
        List<Option> options = Stream.of(
                new Option("A", question.getOptionA()),
                new Option("B", question.getOptionB()),
                new Option("C", question.getOptionC()),
                new Option("D", question.getOptionD()))
            .filter(option -> option.text() != null)
            .toList();

        return new QuestionResponse(
            item.getPosition(),
            totalItems,
            question.getStatement(),
            options,
            timeLimit.toSeconds(),
            item.getStartedAt().plus(timeLimit)
        );
    }
}
