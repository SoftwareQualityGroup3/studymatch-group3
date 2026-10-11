package pt.studymatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.domain.AppStatus;

public interface AppStatusRepository extends JpaRepository<AppStatus, Integer> {

    boolean existsByStatus(String status);
}
