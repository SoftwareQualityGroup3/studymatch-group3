package pt.studymatch.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GroupingContextStudentsRequest(
    @NotEmpty(message = "Student list must not be empty")
    List<@NotNull(message = "Student ids must not contain null") Long> studentIds
) {
}
