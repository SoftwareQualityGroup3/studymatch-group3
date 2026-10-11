package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import pt.studymatch.dto.HealthResponse;
import pt.studymatch.repository.AppStatusRepository;

@ExtendWith(MockitoExtension.class)
class HealthServiceTest {

    @Mock
    private AppStatusRepository appStatusRepository;

    @InjectMocks
    private HealthService healthService;

    @Test
    void returnsUpWhenDatabaseHasUpStatus() {
        when(appStatusRepository.existsByStatus("UP")).thenReturn(true);

        HealthResponse response = healthService.getHealth();

        assertEquals("UP", response.status());
        assertEquals("UP", response.database());
    }

    @Test
    void returnsDownWhenDatabaseHasNoUpStatus() {
        when(appStatusRepository.existsByStatus("UP")).thenReturn(false);

        HealthResponse response = healthService.getHealth();

        assertEquals("DOWN", response.status());
        assertEquals("DOWN", response.database());
    }

    @Test
    void returnsDownWhenDatabaseAccessFails() {
        when(appStatusRepository.existsByStatus("UP"))
                .thenThrow(new DataAccessResourceFailureException("database unavailable"));

        HealthResponse response = healthService.getHealth();

        assertEquals("DOWN", response.status());
        assertEquals("DOWN", response.database());
    }
}
