package pt.studymatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.domain.GroupingContext;

public interface GroupingContextRepository
    extends JpaRepository<GroupingContext, Long> {

    boolean existsByName(String name);
}
