# Decisões do Sprint 3

## S3-09: Alterações aos testes de controllers do Sprint 2

Com a introdução de autenticação (Spring Security, HTTP Basic), toda a API
passou a exigir autenticação, exceto `GET /api/health`. Os testes de controllers
do Sprint 2 (`AcademicRecordControllerTest`, `CourseUnitControllerTest`,
`SelfAssessmentControllerTest` e `HealthControllerTest`) usam `@WebMvcTest` e
falhavam com 401 (pedidos GET) e 403 (pedidos POST/PUT, por causa do CSRF por
omissão do Spring).

Alterações feitas, em cada um:

- `@Import(SecurityConfig.class)`: os testes usam as regras reais de segurança.
- `@WithMockUser` (exceto no `HealthControllerTest`, que valida que o health é público).
- `@MockitoBean AppUserRepository`, exigido pelo `UserDetailsService` do `SecurityConfig`.

**Nenhuma asserção nem comportamento esperado foi alterado.** Os testes
continuam a validar os mesmos códigos HTTP e o mesmo JSON de antes; apenas
passaram a correr num contexto autenticado. Resultado: 116 testes a passar.

A cobertura dos novos comportamentos (401, 403, papéis, criação de utilizadores
e utilizador inicial) fica em `UserControllerTest`, `UserServiceTest` e
`InitialAdminRunnerTest`.
