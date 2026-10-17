package pt.studymatch.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.studymatch.dto.CourseUnitResponse;
import pt.studymatch.service.CourseUnitService;

// controller das unidades curriculares (ucs)
// recebe os pedidos http e passa logo para o service sem lógica nenhuma
// só tem leitura porque o frontend só precisa de listar as ucs
@RestController
@RequestMapping("/api/course-units")
public class CourseUnitController {
    private final CourseUnitService courseUnitService;

    // injeção por construtor que o spring faz sozinho
    public CourseUnitController(CourseUnitService courseUnitService) {
        this.courseUnitService = courseUnitService;
    }

    // get na rota base devolve todas as ucs com o respetivo peso de cada competência
    @GetMapping
    public ResponseEntity<List<CourseUnitResponse>> findAll() {
        List<CourseUnitResponse> courseUnits = courseUnitService.findAll();
        return ResponseEntity.ok(courseUnits);
    }
}
