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

        if (studentRepository.existsByNumber(request.number())) {
            throw new DuplicateStudentNumberException(request.number());
        }

        Student student = new Student(
            request.number(),
            request.name(),
            request.email(),
            request.course(),
            request.curricularYear()
        );

        Student savedStudent = studentRepository.save(student);

        return StudentResponse.from(savedStudent);
    }

    /**
     * Devolve todos os estudantes ou filtra por query e curso.
     */
    public List<StudentResponse> search(String q, String course) {
        return studentRepository.searchStudents(q, course)
            .stream()
            .map(StudentResponse::from)
            .toList();
    }

    /**
     * Mantém compatibilidade caso seja chamado sem parâmetros.
     */
    public List<StudentResponse> findAll() {
        return search(null, null);
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
