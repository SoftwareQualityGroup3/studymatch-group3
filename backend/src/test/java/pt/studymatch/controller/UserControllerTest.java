package pt.studymatch.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import pt.studymatch.config.SecurityConfig;
import pt.studymatch.domain.Role;
import pt.studymatch.dto.CreateUserRequest;
import pt.studymatch.dto.UserResponse;
import pt.studymatch.exception.DuplicateUsernameException;
import pt.studymatch.repository.AppUserRepository;
import pt.studymatch.service.UserService;

// testes da camada web dos endpoints de utilizadores e da segurança
// o service é simulado e vemos só os códigos http, o json e as regras de acesso
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    private static final String VALID_BODY = """
            {
                "username": "docente1",
                "password": "password123",
                "role": "DOCENTE"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // o SecurityConfig precisa deste repositório para o UserDetailsService
    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void meWithoutCredentialsReturns401WithTheStandardBody() throws Exception {
        mockMvc.perform(get("/api/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.fieldErrors.general").value("Authentication is required"));
    }

    @Test
    @WithMockUser(username = "ana", roles = "ESTUDANTE")
    void meReturns200WithTheAuthenticatedUser() throws Exception {
        when(userService.me("ana"))
            .thenReturn(new UserResponse(1L, "ana", Role.ESTUDANTE, null));

        mockMvc.perform(get("/api/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("ana"))
            .andExpect(jsonPath("$.role").value("ESTUDANTE"));
    }

    @Test
    void createWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ESTUDANTE")
    void createWithEstudanteRoleReturns403() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.fieldErrors.general").value("Access denied"));

        verify(userService, never()).create(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser(roles = "DOCENTE")
    void createWithDocenteRoleReturns403() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SERVICOS_ACADEMICOS")
    void createWithServicosAcademicosReturns201AndNeverExposesThePassword() throws Exception {
        when(userService.create(any(CreateUserRequest.class)))
            .thenReturn(new UserResponse(2L, "docente1", Role.DOCENTE, null));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.username").value("docente1"))
            .andExpect(jsonPath("$.role").value("DOCENTE"))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "SERVICOS_ACADEMICOS")
    void createReturns409WhenTheUsernameAlreadyExists() throws Exception {
        when(userService.create(any(CreateUserRequest.class)))
            .thenThrow(new DuplicateUsernameException("docente1"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.fieldErrors.username").exists());
    }

    @Test
    @WithMockUser(roles = "SERVICOS_ACADEMICOS")
    void createReturns400WhenTheBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "",
                        "password": "123",
                        "role": "DOCENTE"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.username").exists())
            .andExpect(jsonPath("$.fieldErrors.password").exists());
    }
}
