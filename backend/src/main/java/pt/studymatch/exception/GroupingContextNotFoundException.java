package pt.studymatch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GroupingContextNotFoundException extends RuntimeException {

    public GroupingContextNotFoundException(Long id) {
        super("Grouping context with id " + id + " was not found");
    }
}
