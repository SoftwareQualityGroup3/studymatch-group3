package pt.studymatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import pt.studymatch.domain.AppUser;
import pt.studymatch.domain.Role;
import pt.studymatch.domain.Student;
import pt.studymatch.dto.CreateUserRequest;
import pt.studymatch.dto.UserResponse;
import pt.studymatch.exception.DuplicateUsernameException;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.repository.AppUserRepository;
import pt.studymatch.repository.StudentRepository;

// testes das regras do UserService com os repositórios simulados
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createEncodesThePasswordAndSavesTheUser() {
        when(passwordEncoder.encode("password123")).thenReturn("HASH");
        when(appUserRepository.save(any(AppUser.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.create(
            new CreateUserRequest("docente1", "password123", Role.DOCENTE, null));

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(captor.capture());

        assertEquals("HASH", captor.getValue().getPasswordHash());
        assertNotEquals("password123", captor.getValue().getPasswordHash());
        assertEquals("docente1", response.username());
        assertEquals(Role.DOCENTE, response.role());
        assertNull(response.studentId());
    }

    @Test
    void createRejectsADuplicateUsername() {
        when(appUserRepository.existsByUsername("ana")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () ->
            userService.create(new CreateUserRequest("ana", "password123", Role.DOCENTE, null)));

        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void createRejectsAStudentIdWhenTheRoleIsNotEstudante() {
        assertThrows(IllegalArgumentException.class, () ->
            userService.create(new CreateUserRequest("prof", "password123", Role.DOCENTE, 5L)));

        verifyNoInteractions(studentRepository);
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void createRejectsAnUnknownStudent() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () ->
            userService.create(new CreateUserRequest("aluno", "password123", Role.ESTUDANTE, 99L)));

        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void createRejectsAStudentThatAlreadyHasAUser() {
        Student student = new Student("a1", "Ana", null, null, 1);
        when(studentRepository.findById(5L)).thenReturn(Optional.of(student));
        when(appUserRepository.existsByStudentId(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
            userService.create(new CreateUserRequest("aluno", "password123", Role.ESTUDANTE, 5L)));

        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void createLinksTheStudentWhenTheRoleIsEstudante() {
        Student student = new Student("a1", "Ana", null, null, 1);
        when(studentRepository.findById(5L)).thenReturn(Optional.of(student));
        when(passwordEncoder.encode("password123")).thenReturn("HASH");
        when(appUserRepository.save(any(AppUser.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        userService.create(new CreateUserRequest("aluno", "password123", Role.ESTUDANTE, 5L));

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(captor.capture());

        assertSame(student, captor.getValue().getStudent());
        assertEquals(Role.ESTUDANTE, captor.getValue().getRole());
    }

    @Test
    void meReturnsTheAuthenticatedUser() {
        AppUser ana = new AppUser("ana", "HASH", Role.DOCENTE, null);
        when(appUserRepository.findByUsername("ana")).thenReturn(Optional.of(ana));

        UserResponse response = userService.me("ana");

        assertEquals("ana", response.username());
        assertEquals(Role.DOCENTE, response.role());
    }

    @Test
    void meFailsWhenTheUserDoesNotExist() {
        when(appUserRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> userService.me("ghost"));
    }
}
