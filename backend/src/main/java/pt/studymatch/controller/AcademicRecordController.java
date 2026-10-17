package pt.studymatch.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.studymatch.dto.AcademicRecordRequest;
import pt.studymatch.dto.AcademicRecordResponse;
import pt.studymatch.service.AcademicRecordService;

// controller dos registos académicos (as notas que o aluno teve a cada uc)
// o registo pertence a um aluno por isso o id do aluno vai no path do url
@RestController
@RequestMapping("/api/students/{studentId}/records")
public class AcademicRecordController {
    private final AcademicRecordService academicRecordService;

    public AcademicRecordController(AcademicRecordService academicRecordService) {
        this.academicRecordService = academicRecordService;
    }

    // post cria um registo novo para o aluno que vem no url
    @PostMapping
    public ResponseEntity<AcademicRecordResponse> create(
        @PathVariable Long studentId,
        // o valid faz o spring validar o corpo com as anotações do dto antes de entrar aqui
        // se algo falhar nem chega a correr o método e devolve 400
        @Valid @RequestBody AcademicRecordRequest request) {
        // toda a lógica de negócio (verificar aluno e uc e duplicados) fica no service
        AcademicRecordResponse record = academicRecordService.create(studentId, request);
        return ResponseEntity
            // 201 porque foi criado um recurso novo e não um simples 200
            .status(HttpStatus.CREATED)
            .body(record);
    }

    // get lista todos os registos do aluno
    @GetMapping
    public ResponseEntity<List<AcademicRecordResponse>> findByStudentId(
        @PathVariable Long studentId) {
        // se o aluno não existir o service lança exceção e fica 404
        List<AcademicRecordResponse> records = academicRecordService.findByStudentId(studentId);
        return ResponseEntity.ok(records);
    }
}
