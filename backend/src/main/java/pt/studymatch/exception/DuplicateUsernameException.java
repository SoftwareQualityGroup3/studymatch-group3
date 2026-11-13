package pt.studymatch.exception;

/**
 * Exceção lançada quando se tenta criar um utilizador
 * com um username que já existe.
 */
public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String username) {
        super("Username " + username + " already exists");
    }
}
