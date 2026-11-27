package pt.studymatch.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.studymatch.domain.Competency;
import pt.studymatch.domain.SessionStatus;
import pt.studymatch.dto.AnswerRequest;
import pt.studymatch.dto.QuestionResponse;
import pt.studymatch.dto.TestSessionResponse;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.exception.TestConflictException;
import pt.studymatch.exception.TestSessionNotFoundException;
import pt.studymatch.exception.TestUnavailableException;
import pt.studymatch.service.LevelTestService;

// testa só a camada web: códigos HTTP e JSON; o serviço é simulado
@WebMvcTest(LevelTestController.class)
class LevelTestControllerTest {

    private static final String URL = "/api/students/1/tests";

    // simula pedidos HTTP sem arrancar um servidor
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LevelTestService levelTestService;

    // resposta de exemplo com o primeiro item
    private static TestSessionResponse firstItem() {
        QuestionResponse item = new QuestionResponse(1, 20, "Pergunta",
            List.of(new QuestionResponse.Option("A", "a")), 60,
            Instant.parse("2026-01-01T10:01:00Z"));
        return new TestSessionResponse(1L, Competency.DATABASES,
            SessionStatus.IN_PROGRESS, item, null);
    }

    @Test
    void startReturnsTheItemWithoutTheCorrectAnswer() throws Exception {
        when(levelTestService.start(1L, Competency.DATABASES)).thenReturn(firstItem());

        mockMvc.perform(post(URL + "/DATABASES"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentItem.position").value(1))
            .andExpect(jsonPath("$.currentItem.correctOption").doesNotExist());
    }

    @Test
    void listReturns200() throws Exception {
        when(levelTestService.results(1L)).thenReturn(List.of());

        mockMvc.perform(get(URL))
            .andExpect(status().isOk());
    }

    @Test
    void unknownStudentReturns404() throws Exception {
        when(levelTestService.results(1L)).thenThrow(new StudentNotFoundException(1L));

        mockMvc.perform(get(URL))
            .andExpect(status().isNotFound());
    }

    @Test
    void missingSessionReturns404() throws Exception {
        when(levelTestService.get(1L, Competency.DATABASES))
            .thenThrow(new TestSessionNotFoundException());

        mockMvc.perform(get(URL + "/DATABASES"))
            .andExpect(status().isNotFound());
    }

    @Test
    void unknownCompetencyReturns400() throws Exception {
        mockMvc.perform(post(URL + "/COOKING"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void emptyAnswerReturns400() throws Exception {
        mockMvc.perform(put(URL + "/DATABASES/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void invalidOptionReturns400() throws Exception {
        when(levelTestService.answer(any(), any(), any(AnswerRequest.class)))
            .thenThrow(new IllegalArgumentException("Invalid option"));

        mockMvc.perform(put(URL + "/DATABASES/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"position\": 1, \"answer\": \"Z\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void conflictReturns409() throws Exception {
        when(levelTestService.start(1L, Competency.DATABASES))
            .thenThrow(new TestConflictException("This test is already finished"));

        mockMvc.perform(post(URL + "/DATABASES"))
            .andExpect(status().isConflict());
    }

    @Test
    void unavailableTestReturns503() throws Exception {
        when(levelTestService.start(1L, Competency.PROGRAMMING))
            .thenThrow(new TestUnavailableException("Not available"));

        mockMvc.perform(post(URL + "/PROGRAMMING"))
            .andExpect(status().isServiceUnavailable());
    }
}
