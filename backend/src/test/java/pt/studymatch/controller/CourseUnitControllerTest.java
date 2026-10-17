package pt.studymatch.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.studymatch.domain.Competency;
import pt.studymatch.dto.CourseUnitResponse;
import pt.studymatch.service.CourseUnitService;

// teste só da camada web
// o webmvctest sobe apenas o controller e o mockmvc sem base de dados nem o resto da aplicação
@WebMvcTest(CourseUnitController.class)
class CourseUnitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    // o service é simulado com mockito porque aqui só queremos testar o controller
    @MockitoBean
    private CourseUnitService courseUnitService;

    // garante que o get devolve 200 e que o json tem o código o nome os ects e os pesos
    @Test
    void listReturns200WithCourseUnitsAndWeights() throws Exception {
        when(courseUnitService.findAll()).thenReturn(List.of(
            new CourseUnitResponse("WEB", "Tecnologias Web", 6,
                Map.of(Competency.PROGRAMMING, 0.8, Competency.SOFTWARE_ENGINEERING, 0.2))));
        mockMvc.perform(get("/api/course-units"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("WEB"))
            .andExpect(jsonPath("$[0].name").value("Tecnologias Web"))
            .andExpect(jsonPath("$[0].ects").value(6))
            .andExpect(jsonPath("$[0].competencyWeights.PROGRAMMING").value(0.8))
            .andExpect(jsonPath("$[0].competencyWeights.SOFTWARE_ENGINEERING").value(0.2));
    }
}
