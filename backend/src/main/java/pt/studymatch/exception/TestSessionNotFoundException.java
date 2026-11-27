package pt.studymatch.exception;

/**
 * Lançada quando o estudante não tem nenhuma sessão nesta competência.
 * Vai ser transformada em 404 (Not Found).
 */
public class TestSessionNotFoundException extends RuntimeException {

    public TestSessionNotFoundException() {
        // A mensagem passa para a classe mãe (RuntimeException).
        super("Test session not found");
    }
}
