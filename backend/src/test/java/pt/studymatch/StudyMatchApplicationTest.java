package pt.studymatch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Valida que o contexto Spring arranca, incluindo a ligação à base de dados
 * e a execução das migrações Flyway. Requer as variáveis de ambiente da BD
 * (usar DB_NAME=studymatch_test).
 */
@SpringBootTest
class StudyMatchApplicationTest {

    @Test
    void contextLoads() {
        // Se o contexto não arrancar, o teste falha.
    }
}
