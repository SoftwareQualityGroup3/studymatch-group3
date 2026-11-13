package pt.studymatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.domain.AppUser;
import pt.studymatch.domain.Role;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByRole(Role role);
}
