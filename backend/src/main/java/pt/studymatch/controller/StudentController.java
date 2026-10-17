package pt.studymatch.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.dto.StudentRequest;
import pt.studymatch.dto.StudentResponse;
import pt.studymatch.service.StudentService;

/**
 * Controller responsável pelos endpoints da API de estudantes.
 *
 * O controller recebe os pedidos HTTP e delega a lógica
 * para o StudentService.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    /**
     * O Spring injeta automaticamente o StudentService.
     */
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Cria um novo estudante.
     *
     * POST /api/students
     */
    @PostMapping
    public ResponseEntity<StudentResponse> create(
        @Valid @RequestBody StudentRequest request) {

        StudentResponse student = studentService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(student);
    }

    /**
     * Devolve todos os estudantes.
     *
     * GET /api/students
     */
    @GetMapping
    public ResponseEntity<List<StudentResponse>> findAll() {

        List<StudentResponse> students = studentService.findAll();

        return ResponseEntity.ok(students);
    }

    /**
     * Devolve um estudante através do id.
     *
     * GET /api/students/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> findById(
        @PathVariable Long id) {

        StudentResponse student = studentService.findById(id);

        return ResponseEntity.ok(student);
    }

    /**
     * Atualiza um estudante existente.
     *
     * PUT /api/students/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody StudentRequest request) {

        StudentResponse student = studentService.update(id, request);

        return ResponseEntity.ok(student);
    }
}
