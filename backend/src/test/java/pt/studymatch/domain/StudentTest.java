package pt.studymatch.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void criaEstudanteValido() {
        Student student = new Student("12345", "João Silva", "joao@email.com", "Engenharia Informática", 3);

        assertEquals("12345", student.getNumber());
        assertEquals("João Silva", student.getName());
        assertEquals("joao@email.com", student.getEmail());
        assertEquals("Engenharia Informática", student.getCourse());
        assertEquals(3, student.getCurricularYear());
    }

    @Test
    void rejeitaEstudanteSemNumero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new Student("", "João Silva", "joao@email.com", "Engenharia Informática", 2)
        );
        assertEquals("number is required", exception.getMessage());
    }

    @Test
    void rejeitaEstudanteSemNome() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new Student("12345", "   ", "joao@email.com", "Engenharia Informática", 2)
        );
        assertEquals("name is required", exception.getMessage());
    }

    @Test
    void rejeitaAnoCurricularAbaixoDoLimite() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new Student("12345", "João Silva", "joao@email.com", "Engenharia Informática", 0)
        );
        assertEquals("curricularYear must be between 1 and 5", exception.getMessage());
    }

    @Test
    void rejeitaAnoCurricularAcimaDoLimite() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            new Student("12345", "João Silva", "joao@email.com", "Engenharia Informática", 6)
        );
        assertEquals("curricularYear must be between 1 and 5", exception.getMessage());
    }
}
