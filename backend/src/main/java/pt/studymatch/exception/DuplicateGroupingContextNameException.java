package pt.studymatch.exception;

public class DuplicateGroupingContextNameException extends RuntimeException {

    public DuplicateGroupingContextNameException(String name) {
        super("Grouping context name '" + name + "' already exists");
    }
}
