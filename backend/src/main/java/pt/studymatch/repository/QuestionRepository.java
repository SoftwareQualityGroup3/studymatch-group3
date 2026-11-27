package pt.studymatch.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.Question;

public interface QuestionRepository extends JpaRepository<Question, String> {

    // Todas as perguntas de uma competência, para sortear o teste.
    List<Question> findByCompetencyOrderByLevelAscVariantAsc(Competency competency);
}
