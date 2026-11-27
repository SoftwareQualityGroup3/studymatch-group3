package pt.studymatch.exception;

/**
 * Lançada quando o teste não pode ser feito neste momento
 * (falta o banco de perguntas ou a execução de código).
 * Vai ser transformada em 503 (Service Unavailable).
 * A tentativa do estudante não é gasta.
 */
public class TestUnavailableException extends RuntimeException {

    public TestUnavailableException(String message) {
        super(message);
    }
}
