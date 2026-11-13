package pt.studymatch.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pt.studymatch.domain.AppUser;
import pt.studymatch.domain.Role;
import pt.studymatch.domain.Student;
import pt.studymatch.dto.CreateUserRequest;
import pt.studymatch.dto.UserResponse;
import pt.studymatch.exception.DuplicateUsernameException;
import pt.studymatch.exception.StudentNotFoundException;
import pt.studymatch.repository.AppUserRepository;
import pt.studymatch.repository.StudentRepository;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository,
                       StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Devolve o utilizador autenticado.
     */
    public UserResponse me(String username) {
        AppUser user = appUserRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalStateException(
                "Authenticated user " + username + " not found"));

        return UserResponse.from(user);
    }

    /**
     * Cria um utilizador. O estudante associado é opcional e só
     * faz sentido para o papel ESTUDANTE.
     */
    public UserResponse create(CreateUserRequest request) {
        String username = request.username().trim();

        if (appUserRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(username);
        }

        Student student = null;
        if (request.studentId() != null) {
            if (request.role() != Role.ESTUDANTE) {
                throw new IllegalArgumentException(
                    "studentId can only be used with the ESTUDANTE role");
            }
            student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new StudentNotFoundException(request.studentId()));

            if (appUserRepository.existsByStudentId(student.getId())) {
                throw new IllegalArgumentException(
                    "Student with id " + student.getId() + " already has a user");
            }
        }

        AppUser user = new AppUser(
            username,
            passwordEncoder.encode(request.password()),
            request.role(),
            student
        );

        return UserResponse.from(appUserRepository.save(user));
    }
}
