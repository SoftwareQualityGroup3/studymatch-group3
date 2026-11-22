package pt.studymatch.dto;

import pt.studymatch.domain.GroupingContext;
import pt.studymatch.domain.Student;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record GroupingContextResponse(
    Long id,
    String name,
    String courseUnitCode,
    Instant createdAt,
    List<Long> studentIds
) {
    public static GroupingContextResponse from(GroupingContext context) {
        List<Long> ids = context.getEligibleStudents().stream()
            .map(Student::getId)
            .sorted(Comparator.nullsLast(Comparator.naturalOrder()))
            .toList();

        return new GroupingContextResponse(
            context.getId(),
            context.getName(),
            context.getCourseUnit() == null
                ? null
                : context.getCourseUnit().getCode(),
            context.getCreatedAt(),
            ids
        );
    }
}
