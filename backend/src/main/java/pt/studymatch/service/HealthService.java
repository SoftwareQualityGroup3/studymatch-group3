package pt.studymatch.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pt.studymatch.dto.HealthResponse;
import pt.studymatch.repository.AppStatusRepository;

@Service
public class HealthService {

    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";

    private final AppStatusRepository appStatusRepository;

    public HealthService(AppStatusRepository appStatusRepository) {
        this.appStatusRepository = appStatusRepository;
    }

    public HealthResponse getHealth() {
        try {
            if (appStatusRepository.existsByStatus(STATUS_UP)) {
                return new HealthResponse(STATUS_UP, STATUS_UP);
            }

            return new HealthResponse(STATUS_DOWN, STATUS_DOWN);
        } catch (DataAccessException exception) {
            return new HealthResponse(STATUS_DOWN, STATUS_DOWN);
        }
    }
}
