package pt.studymatch.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.domain.Competency;
import pt.studymatch.domain.SelfAssessment;
import pt.studymatch.domain.Student;
import pt.studymatch.dto.SelfAssessmentRequest;
import pt.studymatch.dto.SelfAssessmentResponse;
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

    //guarda de uma só vez as notas da autoavaliação que foram enviadas
    //basta uma competência: as que não vêm no pedido ficam como estavam
    //se uma das competências falhar, nenhuma fica guardada e a autoavaliação não fica a meio
    //repetir o pedido com os mesmos valores dá o mesmo resultado
    @Transactional
    public SelfAssessmentResponse saveAll(
        Long studentId,
        SelfAssessmentRequest request) {

        Map<Competency, Integer> values = request.toValues();

        // Pedido sem nenhuma competência não tem nada para guardar (400).
        if (values.isEmpty()) {
            throw new IllegalArgumentException(
                "At least one competency must be provided");
        }

        // Cada competência enviada reutiliza o saveOrUpdate:
        // cria se não existir, atualiza se já existir.
        // Estudante inexistente lança StudentNotFoundException (404).
        values.forEach((competency, value) ->
            saveOrUpdate(studentId, competency, value));

        return SelfAssessmentResponse.from(
            selfAssessmentRepository.findByStudentId(studentId));
    }

    //devolve a autoavaliação de um estudante já em formato de resposta
    //um estudante sem autoavaliação recebe os valores a null
    //um estudante que não existe lança StudentNotFoundException (404)
    @Transactional(readOnly = true)
    public SelfAssessmentResponse getByStudentId(Long studentId) {

        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }

        return SelfAssessmentResponse.from(
            selfAssessmentRepository.findByStudentId(studentId));
    }
}
