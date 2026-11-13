package pt.studymatch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pt.studymatch.domain.AppUser;
import pt.studymatch.domain.Role;
import pt.studymatch.repository.AppUserRepository;

/**
 * Cria o primeiro utilizador de Serviços Académicos, a partir de
 * variáveis de ambiente, se ainda não existir nenhum.
 */
@Component
public class InitialAdminRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(InitialAdminRunner.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public InitialAdminRunner(AppUserRepository appUserRepository,
                              PasswordEncoder passwordEncoder,
                              @Value("${studymatch.bootstrap.admin-username:}") String adminUsername,
                              @Value("${studymatch.bootstrap.admin-password:}") String adminPassword) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (appUserRepository.existsByRole(Role.SERVICOS_ACADEMICOS)) {
            return;
        }

        if (adminUsername.isBlank() || adminPassword.isBlank()) {
            log.warn("Não existe nenhum utilizador de Serviços Académicos e "
                + "ADMIN_USERNAME/ADMIN_PASSWORD não estão definidos.");
            return;
        }

        appUserRepository.save(new AppUser(
            adminUsername,
            passwordEncoder.encode(adminPassword),
            Role.SERVICOS_ACADEMICOS,
            null
        ));
        log.info("Criado o utilizador inicial de Serviços Académicos '{}'.", adminUsername);
    }
}
