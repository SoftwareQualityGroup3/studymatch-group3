package pt.studymatch.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.studymatch.dto.SelfAssessmentRequest;
import pt.studymatch.dto.SelfAssessmentResponse;
import pt.studymatch.service.SelfAssessmentService;

// controller da autoavaliação do estudante (onboarding / cold start)
// a autoavaliação pertence a um estudante, por isso o id dele vai no path do url
@RestController
@RequestMapping("/api/students/{studentId}/self-assessment")
public class SelfAssessmentController {

    private final SelfAssessmentService selfAssessmentService;

    public SelfAssessmentController(SelfAssessmentService selfAssessmentService) {
        this.selfAssessmentService = selfAssessmentService;
    }

    // put guarda as quatro notas (cria ou substitui as que já existiam)
    // é put e não post porque repetir o pedido dá sempre o mesmo resultado
    // o valid faz o spring rejeitar valores fora de 1-5 com 400
    @PutMapping
    public ResponseEntity<SelfAssessmentResponse> save(
        @PathVariable Long studentId,
        @Valid @RequestBody SelfAssessmentRequest request) {

        return ResponseEntity.ok(selfAssessmentService.saveAll(studentId, request));
    }

    // get devolve a autoavaliação atual do estudante
    // se o estudante não existir o service lança exceção e fica 404
    @GetMapping
    public ResponseEntity<SelfAssessmentResponse> find(@PathVariable Long studentId) {
        return ResponseEntity.ok(selfAssessmentService.getByStudentId(studentId));
    }
}
