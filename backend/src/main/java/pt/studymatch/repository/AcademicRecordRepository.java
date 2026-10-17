package pt.studymatch.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.domain.AcademicRecord;

// repository dos registos académicos
// os métodos são gerados pelo spring só a partir do nome deles sem escrever sql
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Long> {
    // verifica se o aluno já tem registo naquela uc e é usado para evitar duplicados
    // o underscore diz ao spring para entrar na relação (student e depois id)
    boolean existsByStudent_IdAndCourseUnit_Code(Long studentId, String courseUnitCode);
    // devolve todos os registos de um aluno
    List<AcademicRecord> findByStudent_Id(Long studentId);
}
