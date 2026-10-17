package pt.studymatch.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.studymatch.dto.AcademicRecordRequest;
import pt.studymatch.dto.AcademicRecordResponse;
import pt.studymatch.exception.CourseUnitNotFoundException;
import pt.studymatch.exception.DuplicateAcademicRecordException;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.service.AcademicRecordService;

// testes da camada web do controller dos registos
// o service é simulado e vemos só se os códigos http e o json estão certos
@WebMvcTest(AcademicRecordController.class)
class AcademicRecordControllerTest {
    private static final String URL = "/api/students/1/records";
    // corpo válido reutilizado nos testes em que o que importa é o que o service faz
    private static final String VALID_BODY = """
            {
                "courseUnitCode": "PROG1",
                "grade": 15.5,
                "academicYear": "2024/25",
                "semester": 1
            }
            """;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AcademicRecordService academicRecordService;

    // monta um corpo de pedido com os valores que quisermos para testar casos inválidos
    private static String body(String grade, String academicYear, String semester) {
        return """
                {
                    "courseUnitCode": "PROG1",
                    "grade": %s,
                    "academicYear": %s,
                    "semester": %s
                }
                """.formatted(grade, academicYear, semester);
    }

    // caminho feliz que dá 201 e devolve o registo criado
    @Test
    void createReturns201WithTheCreatedRecord() throws Exception {
        when(academicRecordService.create(eq(1L), any(AcademicRecordRequest.class)))
            .thenReturn(new AcademicRecordResponse(
                1L, "PROG1", "Programação I", "2024/25", 1, new BigDecimal("15.50")));
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.courseUnitCode").value("PROG1"))
            .andExpect(jsonPath("$.courseUnitName").value("Programação I"))
            .andExpect(jsonPath("$.academicYear").value("2024/25"))
            .andExpect(jsonPath("$.semester").value(1))
            .andExpect(jsonPath("$.grade").value(15.5));
    }

    // as próximas validações vêm das anotações do dto e dão 400 sem chegar ao service
    // nota acima de 20
    @Test
    void createReturns400WhenGradeIsAboveTwenty() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("20.5", "\"2024/25\"", "1")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.grade").value("grade must be between 0 and 20"));
    }

    // nota abaixo de 0
    @Test
    void createReturns400WhenGradeIsNegative() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("-1", "\"2024/25\"", "1")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.grade").value("grade must be between 0 and 20"));
    }

    // semestre que não seja 1 nem 2
    @Test
    void createReturns400WhenSemesterIsInvalid() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("15", "\"2024/25\"", "3")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.semester").value("semester must be 1 or 2"));
    }

    // ano letivo com hífen em vez de barra
    @Test
    void createReturns400WhenAcademicYearHasWrongFormat() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("15", "\"2024-25\"", "1")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.academicYear").exists());
    }

    // corpo vazio e tem de dar um erro por cada campo obrigatório
    @Test
    void createReturns400WhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.courseUnitCode").value("courseUnitCode is required"))
            .andExpect(jsonPath("$.fieldErrors.grade").value("grade is required"))
            .andExpect(jsonPath("$.fieldErrors.academicYear").value("academicYear is required"))
            .andExpect(jsonPath("$.fieldErrors.semester").value("semester is required"));
    }

    // aqui o erro vem do domínio e não do dto
    // 2024/30 passa no regex mas o domínio rejeita porque o segundo ano devia ser 25
    // o handler global converte a illegalargumentexception em 400
    @Test
    void createReturns400WhenDomainRejectsTheData() throws Exception {
        when(academicRecordService.create(eq(1L), any(AcademicRecordRequest.class)))
            .thenThrow(new IllegalArgumentException("Academic year must end in the year after it starts"));
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(body("15", "\"2024/30\"", "1")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.general").exists());
    }

    // aluno inexistente dá 404
    @Test
    void createReturns404WhenStudentDoesNotExist() throws Exception {
        when(academicRecordService.create(eq(1L), any(AcademicRecordRequest.class)))
            .thenThrow(new StudentNotFoundException(1L));
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.fieldErrors.id").value("Student with id 1 was not found"));
    }

    // uc inexistente também dá 404
    @Test
    void createReturns404WhenCourseUnitDoesNotExist() throws Exception {
        when(academicRecordService.create(eq(1L), any(AcademicRecordRequest.class)))
            .thenThrow(new CourseUnitNotFoundException("PROG1"));
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.fieldErrors.courseUnitCode")
                .value("Course unit with code PROG1 was not found"));
    }

    // registo repetido dá 409 conflito
    @Test
    void createReturns409WhenRecordAlreadyExists() throws Exception {
        when(academicRecordService.create(eq(1L), any(AcademicRecordRequest.class)))
            .thenThrow(new DuplicateAcademicRecordException(1L, "PROG1"));
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.fieldErrors.courseUnitCode").exists());
    }

    // listagem normal com 200
    @Test
    void listReturns200WithTheRecords() throws Exception {
        when(academicRecordService.findByStudentId(1L)).thenReturn(List.of(
            new AcademicRecordResponse(1L, "PROG1", "Programação I", "2024/25", 1, new BigDecimal("15.50"))));
        mockMvc.perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].courseUnitName").value("Programação I"))
            .andExpect(jsonPath("$[0].grade").value(15.5));
    }

    // listar registos de um aluno que não existe dá 404
    @Test
    void listReturns404WhenStudentDoesNotExist() throws Exception {
        when(academicRecordService.findByStudentId(99L)).thenThrow(new StudentNotFoundException(99L));
        mockMvc.perform(get("/api/students/99/records"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.fieldErrors.id").value("Student with id 99 was not found"));
    }
}
