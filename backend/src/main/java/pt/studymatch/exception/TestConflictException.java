package pt.studymatch.exception;

/**
 * Lançada quando o pedido não é compatível com o estado do teste,
 * por exemplo responder a um teste já terminado.
 * Vai ser transformada em 409 (Conflict).
 */
public class TestConflictException extends RuntimeException {

    // Recebe a mensagem porque há vários motivos de conflito.
    public TestConflictException(String message) {
        super(message);
    }
}
