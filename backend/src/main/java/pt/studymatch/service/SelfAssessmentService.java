package pt.studymatch.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.domain.Competency;
import pt.studymatch.domain.SelfAssessment;
import pt.studymatch.domain.Student;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.repository.SelfAssessmentRepository;
import pt.studymatch.repository.StudentRepository;

@Service
public class SelfAssessmentService {

    private final SelfAssessmentRepository selfAssessmentRepository;
    private final StudentRepository studentRepository;

    public SelfAssessmentService(
        SelfAssessmentRepository selfAssessmentRepository,
        StudentRepository studentRepository) {

        this.selfAssessmentRepository = selfAssessmentRepository;
        this.studentRepository = studentRepository;
    }

    /*
     * Cria uma nova autoavaliação ou atualiza a existente.
     *
     * Para o mesmo estudante + competência só pode existir
     * uma autoavaliação.
     */
    @Transactional
    public SelfAssessment saveOrUpdate(
        Long studentId,
        Competency competency,
        int value) {

        // Primeiro confirmamos que o estudante existe.
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() ->
                new StudentNotFoundException(studentId));

        // Procuramos se já existe uma autoavaliação
        // para este estudante nesta competência.
        return selfAssessmentRepository
            .findByStudentIdAndCompetency(
                studentId,
                competency)
            .map(existing -> {

                // Já existe:
                // apenas substituímos o valor anterior.
                existing.updateValue(value);

                return selfAssessmentRepository.save(existing);
            })
            .orElseGet(() -> {

                // Não existe:
                // criamos uma nova autoavaliação.
                SelfAssessment selfAssessment =
                    new SelfAssessment(
                        student,
                        competency,
                        value);

                return selfAssessmentRepository.save(selfAssessment);
            });
    }

    /*
     * Devolve todas as autoavaliações
     * de um determinado estudante.
     */
    public List<SelfAssessment> findByStudentId(Long studentId) {
        return selfAssessmentRepository.findByStudentId(studentId);
    }
}
