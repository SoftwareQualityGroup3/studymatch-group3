package pt.studymatch.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.studymatch.dto.SelfAssessmentRequest;
import pt.studymatch.dto.SelfAssessmentResponse;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.service.SelfAssessmentService;

// testes da camada web do controller da autoavaliação
// o service é simulado e vemos só se os códigos http e o json estão certos
@WebMvcTest(SelfAssessmentController.class)
class SelfAssessmentControllerTest {

    private static final String URL = "/api/students/1/self-assessment";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SelfAssessmentService selfAssessmentService;

    // monta um corpo de pedido com os valores que quisermos para testar casos inválidos
    private static String body(String programming, String mathematics,
                               String databases, String softwareEngineering) {
        return """
                {
                    "programming": %s,
                    "mathematics": %s,
                    "databases": %s,
                    "softwareEngineering": %s
                }
                """.formatted(programming, mathematics, databases, softwareEngineering);
    }

    // caminho feliz do put: 200 e devolve os valores guardados
    @Test
    void putReturns200WithTheSavedValues() throws Exception {
        when(selfAssessmentService.saveAll(eq(1L), any(SelfAssessmentRequest.class)))
            .thenReturn(new SelfAssessmentResponse(3, 4, 2, 5));

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("3", "4", "2", "5")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.programming").value(3))
            .andExpect(jsonPath("$.mathematics").value(4))
            .andExpect(jsonPath("$.databases").value(2))
            .andExpect(jsonPath("$.softwareEngineering").value(5));
    }

    // os limites 1 e 5 são válidos
    @Test
    void putAcceptsTheLimitValuesOneAndFive() throws Exception {
        when(selfAssessmentService.saveAll(eq(1L), any(SelfAssessmentRequest.class)))
            .thenReturn(new SelfAssessmentResponse(1, 5, 1, 5));

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("1", "5", "1", "5")))
            .andExpect(status().isOk());
    }

    // as próximas validações vêm das anotações do dto e dão 400 sem chegar ao service
    // valor acima de 5
    @Test
    void putReturns400WhenAValueIsAboveFive() throws Exception {
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("6", "4", "2", "5")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.programming")
                .value("programming must be between 1 and 5"));

        verify(selfAssessmentService, never()).saveAll(any(), any());
    }

    // valor abaixo de 1 (o 0 é o caso mais fácil de esquecer)
    @Test
    void putReturns400WhenAValueIsZero() throws Exception {
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("3", "0", "2", "5")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.mathematics")
                .value("mathematics must be between 1 and 5"));

        verify(selfAssessmentService, never()).saveAll(any(), any());
    }

    // valor negativo
    @Test
    void putReturns400WhenAValueIsNegative() throws Exception {
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("3", "4", "-1", "5")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.databases")
                .value("databases must be between 1 and 5"));
    }

    // basta preencher uma competência: as outras podem faltar e o pedido é aceite
    @Test
    void putAcceptsARequestWithOnlyOneCompetency() throws Exception {
        when(selfAssessmentService.saveAll(eq(1L), any(SelfAssessmentRequest.class)))
            .thenReturn(new SelfAssessmentResponse(3, null, null, null));

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "programming": 3 }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.programming").value(3))
            .andExpect(jsonPath("$.mathematics").isEmpty());
    }

    // uma competência em falta não impede que as enviadas sejam validadas
    @Test
    void putStillValidatesTheCompetenciesThatWereSent() throws Exception {
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "programming": 3, "databases": 9 }
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.databases")
                .value("databases must be between 1 and 5"));

        verify(selfAssessmentService, never()).saveAll(any(), any());
    }

    // pedido sem nenhuma competência: o service rejeita e o handler devolve 400
    @Test
    void putReturns400WhenNoCompetencyIsSent() throws Exception {
        when(selfAssessmentService.saveAll(eq(1L), any(SelfAssessmentRequest.class)))
            .thenThrow(new IllegalArgumentException("At least one competency must be provided"));

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.general")
                .value("At least one competency must be provided"));
    }

    // estudante que não existe dá 404 (o service lança a exceção)
    @Test
    void putReturns404WhenTheStudentDoesNotExist() throws Exception {
        when(selfAssessmentService.saveAll(eq(1L), any(SelfAssessmentRequest.class)))
            .thenThrow(new StudentNotFoundException(1L));

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("3", "4", "2", "5")))
            .andExpect(status().isNotFound());
    }

    // caminho feliz do get
    @Test
    void getReturns200WithTheStudentSelfAssessment() throws Exception {
        when(selfAssessmentService.getByStudentId(1L))
            .thenReturn(new SelfAssessmentResponse(3, 4, 2, 5));

        mockMvc.perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.programming").value(3))
            .andExpect(jsonPath("$.softwareEngineering").value(5));
    }

    // estudante novo, ainda sem autoavaliação: 200 com tudo a null (não é um erro)
    @Test
    void getReturns200WithNullsWhenThereIsNoSelfAssessmentYet() throws Exception {
        when(selfAssessmentService.getByStudentId(1L))
            .thenReturn(new SelfAssessmentResponse(null, null, null, null));

        mockMvc.perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.programming").isEmpty())
            .andExpect(jsonPath("$.mathematics").isEmpty())
            .andExpect(jsonPath("$.databases").isEmpty())
            .andExpect(jsonPath("$.softwareEngineering").isEmpty());
    }

    // estudante que não existe dá 404
    @Test
    void getReturns404WhenTheStudentDoesNotExist() throws Exception {
        when(selfAssessmentService.getByStudentId(1L))
            .thenThrow(new StudentNotFoundException(1L));

        mockMvc.perform(get(URL))
            .andExpect(status().isNotFound());
    }
}
