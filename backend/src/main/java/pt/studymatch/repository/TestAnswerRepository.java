package pt.studymatch.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.domain.TestAnswer;

public interface TestAnswerRepository extends JpaRepository<TestAnswer, Long> {

    // Itens de uma sessão pela ordem em que são mostrados.
    List<TestAnswer> findBySessionIdOrderByPosition(Long sessionId);
}
