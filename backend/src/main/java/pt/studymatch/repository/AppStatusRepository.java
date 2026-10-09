package pt.studymatch.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppStatusRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppStatusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isDatabaseUp() {
        Integer numberOfRows = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app_status WHERE status = 'UP'",
                Integer.class
        );

        return numberOfRows != null && numberOfRows > 0;
    }
}