package pt.studymatch.exception;

/**
 * Exceção lançada quando é pedido um estudante
 * que não existe na base de dados.
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Long id) {
        super("Student with id " + id + " was not found");
    }
}
