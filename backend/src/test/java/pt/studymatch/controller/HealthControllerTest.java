package pt.studymatch.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.studymatch.dto.HealthResponse;
import pt.studymatch.service.HealthService;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HealthService healthService;

    @Test
    void healthReturnsUpWhenDatabaseIsAvailable() throws Exception {
        when(healthService.getHealth())
                .thenReturn(new HealthResponse("UP", "UP"));

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "status": "UP",
                            "database": "UP"
                        }
                        """));
    }

    @Test
    void healthReturnsServiceUnavailableWhenDatabaseIsDown() throws Exception {
        when(healthService.getHealth())
                .thenReturn(new HealthResponse("DOWN", "DOWN"));

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json("""
                        {
                            "status": "DOWN",
                            "database": "DOWN"
                        }
                        """));
    }
}