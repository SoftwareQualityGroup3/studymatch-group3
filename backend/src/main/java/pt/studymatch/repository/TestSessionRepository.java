package pt.studymatch.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.TestSession;

public interface TestSessionRepository extends JpaRepository<TestSession, Long> {

    // Há no máximo uma sessão por estudante e competência.
    Optional<TestSession> findByStudentIdAndCompetency(Long studentId, Competency competency);

    // Para listar os resultados de um estudante.
    List<TestSession> findByStudentId(Long studentId);
}
