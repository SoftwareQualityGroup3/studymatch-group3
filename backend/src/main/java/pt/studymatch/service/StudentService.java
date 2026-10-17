package pt.studymatch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pt.studymatch.domain.Student;
import pt.studymatch.dto.StudentRequest;
import pt.studymatch.dto.StudentResponse;
import pt.studymatch.repository.StudentRepository;
import pt.studymatch.exception.DuplicateStudentNumberException;
import pt.studymatch.exception.StudentNotFoundException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Cria um novo estudante.
     */
    public StudentResponse create(StudentRequest request) {

        // Não pode existir outro estudante com o mesmo número.
        if (studentRepository.existsByNumber(request.number())) {
            throw new DuplicateStudentNumberException(request.number());
        }

        // Converte os dados recebidos no DTO para a entidade Student.
        Student student = new Student(
            request.number(),
            request.name(),
            request.email(),
            request.course(),
            request.curricularYear()
        );

        // Guarda o estudante na base de dados.
        Student savedStudent = studentRepository.save(student);

        // Converte a entidade guardada para o DTO que será devolvido pela API.
        return StudentResponse.from(savedStudent);
    }

    /**
     * Devolve todos os estudantes.
     */
    public List<StudentResponse> findAll() {
        return studentRepository.findAll()
            .stream()
            .map(StudentResponse::from)
            .toList();
    }

    /**
     * Procura um estudante pelo id.
     */
    public StudentResponse findById(Long id) {

        Student student = studentRepository.findById(id)
            .orElseThrow(() ->
                new StudentNotFoundException(id));

        return StudentResponse.from(student);
    }

    /**
     * Atualiza um estudante existente.
     */
    public StudentResponse update(Long id, StudentRequest request) {

        Student student = studentRepository.findById(id)
            .orElseThrow(() ->
                new StudentNotFoundException(id));

        // Permite manter o mesmo número do próprio estudante,
        // mas impede usar o número de outro estudante.
        if (studentRepository.existsByNumberAndIdNot(request.number(), id)) {
            throw new DuplicateStudentNumberException(request.number());
        }

        student.update(
            request.number(),
            request.name(),
            request.email(),
            request.course(),
            request.curricularYear()
        );

        Student savedStudent = studentRepository.save(student);

        return StudentResponse.from(savedStudent);
    }
}
