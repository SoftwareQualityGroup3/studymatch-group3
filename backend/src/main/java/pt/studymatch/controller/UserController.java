package pt.studymatch.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.dto.CreateUserRequest;
import pt.studymatch.dto.UserResponse;
import pt.studymatch.service.UserService;

/**
 * Endpoints de utilizadores e papéis.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Devolve o utilizador autenticado e o seu papel.
     */
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return userService.me(authentication.getName());
    }

    /**
     * Cria um utilizador (apenas Serviços Académicos).
     */
    @PostMapping("/users")
    @PreAuthorize("hasRole('SERVICOS_ACADEMICOS')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }
}
