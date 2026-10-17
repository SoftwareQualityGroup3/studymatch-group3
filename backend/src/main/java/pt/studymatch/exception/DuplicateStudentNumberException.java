package pt.studymatch.exception;

/**
 * Exceção lançada quando se tenta usar um número
 * de estudante que já pertence a outro estudante.
 */
public class DuplicateStudentNumberException extends RuntimeException {

    public DuplicateStudentNumberException(String number) {
        super("Student number " + number + " already exists");
    }
}
