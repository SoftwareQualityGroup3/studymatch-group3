package pt.studymatch.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pt.studymatch.dto.HealthResponse;
import pt.studymatch.repository.AppStatusRepository;

@Service
public class HealthService {

    private final AppStatusRepository appStatusRepository;

    public HealthService(AppStatusRepository appStatusRepository) {
        this.appStatusRepository = appStatusRepository;
    }

    public HealthResponse getHealth() {
        try {
            boolean databaseIsUp = appStatusRepository.isDatabaseUp();

            if (databaseIsUp) {
                return new HealthResponse("UP", "UP");
            }

            return new HealthResponse("DOWN", "DOWN");
        } catch (DataAccessException exception) {
            return new HealthResponse("DOWN", "DOWN");
        }
    }
}