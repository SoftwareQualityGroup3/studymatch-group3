package pt.studymatch.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import pt.studymatch.domain.AppUser;
import pt.studymatch.domain.Role;
import pt.studymatch.repository.AppUserRepository;

// testes do utilizador inicial de Serviços Académicos
@ExtendWith(MockitoExtension.class)
class InitialAdminRunnerTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void createsTheAdminWhenNoneExists() {
        when(appUserRepository.existsByRole(Role.SERVICOS_ACADEMICOS)).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("HASH");

        new InitialAdminRunner(appUserRepository, passwordEncoder, "admin", "secret123")
            .run(null);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(captor.capture());

        assertEquals("admin", captor.getValue().getUsername());
        assertEquals("HASH", captor.getValue().getPasswordHash());
        assertEquals(Role.SERVICOS_ACADEMICOS, captor.getValue().getRole());
    }

    @Test
    void doesNothingWhenAnAdminAlreadyExists() {
        when(appUserRepository.existsByRole(Role.SERVICOS_ACADEMICOS)).thenReturn(true);

        new InitialAdminRunner(appUserRepository, passwordEncoder, "admin", "secret123")
            .run(null);

        verify(appUserRepository, never()).save(any(AppUser.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void doesNothingWhenTheEnvironmentVariablesAreMissing() {
        new InitialAdminRunner(appUserRepository, passwordEncoder, "", "")
            .run(null);

        verify(appUserRepository, never()).save(any(AppUser.class));
        verifyNoInteractions(passwordEncoder);
    }
}
