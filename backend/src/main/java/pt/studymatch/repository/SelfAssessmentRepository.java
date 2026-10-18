package pt.studymatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.domain.Competency;
import pt.studymatch.domain.SelfAssessment;

public interface SelfAssessmentRepository
    extends JpaRepository<SelfAssessment, Long> {

    // Procura a autoavaliação de um estudante
    // numa competência específica.
    Optional<SelfAssessment> findByStudentIdAndCompetency(
        Long studentId,
        Competency competency);

    // Procura todas as autoavaliações de um estudante.
    // Vai ser útil para calcular o perfil completo.
    List<SelfAssessment> findByStudentId(Long studentId);
}
