package pt.studymatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GroupingContextCreateRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must have at most 150 characters")
    String name,

    @Size(max = 50, message = "Course unit code must have at most 50 characters")
    String courseUnitCode,

    @NotEmpty(message = "Student list must not be empty")
    List<@NotNull(message = "Student ids must not contain null") Long> studentIds
) {
}
