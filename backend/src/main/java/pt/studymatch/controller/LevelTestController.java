package pt.studymatch.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.studymatch.domain.Competency;
import pt.studymatch.dto.AnswerRequest;
import pt.studymatch.dto.TestResultResponse;
import pt.studymatch.dto.TestSessionResponse;
import pt.studymatch.service.LevelTestService;

/**
 * Endpoints dos testes de nível.
 * O controller só recebe o pedido e passa-o ao serviço; as regras estão lá.
 */
@RestController
@RequestMapping("/api/students/{studentId}/tests") // início comum a todos os URLs
public class LevelTestController {

    private final LevelTestService levelTestService;

    public LevelTestController(LevelTestService levelTestService) {
        this.levelTestService = levelTestService;
    }

    // Lista os testes do estudante e os resultados.
    @GetMapping
    public List<TestResultResponse> results(@PathVariable Long studentId) {
        return levelTestService.results(studentId);
    }

    // Começa o teste (ou retoma) e devolve só o item atual.
    // O Spring converte o texto do URL no enum; um nome desconhecido dá 400.
    @PostMapping("/{competency}")
    public TestSessionResponse start(@PathVariable Long studentId,
                                     @PathVariable Competency competency) {
        return levelTestService.start(studentId, competency);
    }

    // Estado atual da sessão, para retomar.
    @GetMapping("/{competency}")
    public TestSessionResponse get(@PathVariable Long studentId,
                                   @PathVariable Competency competency) {
        return levelTestService.get(studentId, competency);
    }

    // Responde ao item atual. @Valid verifica o @NotNull e o @NotBlank do pedido.
    @PutMapping("/{competency}/answer")
    public TestSessionResponse answer(@PathVariable Long studentId,
                                      @PathVariable Competency competency,
                                      @Valid @RequestBody AnswerRequest request) {
        return levelTestService.answer(studentId, competency, request);
    }
}
