# ADR-006: Papéis de Utilizador e Autenticação

## Estado

Proposto

## Contexto

Até ao Sprint 2, a API do StudyMatch era totalmente aberta: qualquer cliente podia
ler e alterar estudantes, registos académicos e autoavaliações.

Os casos de uso definem três atores com responsabilidades diferentes:

- **Estudante**: consulta e atualiza os seus próprios dados;
- **Docente**: consulta informação dos estudantes;
- **Serviços Académicos**: gere a informação académica e os utilizadores.

É necessário identificar quem faz cada pedido e distinguir o que cada ator pode fazer.
A solução deve ser simples de implementar e de testar neste sprint, compatível com a
arquitetura em camadas do ADR-002 e com o frontend SPA.

## Decisão

### Papéis

Existem três papéis, representados pelo enum `Role`:

| Papel | Significado |
|---|---|
| `ESTUDANTE` | Estudante da instituição |
| `DOCENTE` | Docente da instituição |
| `SERVICOS_ACADEMICOS` | Serviços Académicos (administração) |

Cada utilizador tem **um único papel**.

### Modelo de dados

A tabela `app_user` (migração `V6`) guarda: `username` (único), `password_hash`
(BCrypt), `role` e `student_id` (opcional e único). Um estudante tem no máximo um
utilizador associado.

O `student_id` só é aceite para o papel `ESTUDANTE`. Esta regra é validada no
`UserService` e não no SQL, para devolver uma resposta 400 com mensagem clara e para
ser fácil de mudar.

### Mecanismo de autenticação

- **HTTP Basic**: cada pedido leva o header `Authorization`.
- **Stateless**: sem sessões nem cookies de autenticação.
- **Passwords** guardadas apenas como hash **BCrypt**.
- **CSRF desativado**: a API não usa sessões nem cookies, e o header `Authorization`
  não é enviado automaticamente pelo browser para outros sites, logo não existe o
  ataque CSRF que esta proteção previne.
- **CORS** permitido apenas para a origem do frontend (`http://localhost:5173` por
  omissão, configurável com `studymatch.cors.allowed-origin`).

### Regras de acesso

| Endpoint | Acesso |
|---|---|
| `GET /api/health` | Público |
| `GET /api/me` | Qualquer utilizador autenticado |
| `POST /api/users` | Apenas `SERVICOS_ACADEMICOS` |
| Restantes `/api/**` | Qualquer utilizador autenticado |

Neste sprint, os endpoints do Sprint 2 exigem apenas autenticação. A distinção por
papel nesses endpoints fica para a S3-28.

### Respostas de erro

- **401** (sem credenciais ou credenciais inválidas) e **403** (papel sem permissão)
  usam o mesmo corpo de erro do resto da API (`{"fieldErrors": {"general": "..."}}`).
- Os erros de segurança acontecem nos filtros, antes de chegarem aos controllers. Por
  isso o `AuthenticationEntryPoint` e o `AccessDeniedHandler` entregam a exceção ao
  `HandlerExceptionResolver`, e o `GlobalExceptionHandler` produz o corpo, num só sítio.

### Utilizador inicial

Se não existir nenhum utilizador de `SERVICOS_ACADEMICOS`, o arranque cria um a partir
das variáveis de ambiente `ADMIN_USERNAME` e `ADMIN_PASSWORD` (ver `.env.example`).
Sem estas variáveis, nada é criado e o arranque regista um aviso. Os restantes
utilizadores são criados por `POST /api/users`.

## Consequências

### Positivas

- Toda a API (exceto o health) passa a estar protegida.
- Solução simples, sem dependências além do Spring Security.
- Compatível com a arquitetura em camadas: a segurança fica em `config`, a lógica de
  utilizadores em `service`, e os erros no `GlobalExceptionHandler`.
- Fácil de testar com `@WithMockUser` e `@WebMvcTest`.
- As passwords nunca são guardadas nem devolvidas em texto.

### Negativas / Trade-offs

- A password segue em **cada pedido**: em produção é obrigatório usar **HTTPS**.
- Não existe logout nem expiração de credenciais.
- Não há proteção contra tentativas repetidas de autenticação (força bruta).
- O frontend tem de guardar as credenciais para as enviar em cada pedido.
- Cada utilizador tem um único papel.
- Não há recuperação nem alteração de password.
- Os endpoints do Sprint 2 ainda não distinguem papéis (só autenticação).
- Os testes de controllers do Sprint 2 tiveram de ser adaptados ao novo contexto
  de segurança (ver `docs/decisoes-sprint3.md`).

## Alternativas Consideradas

### JWT

Permitiria autenticação sem enviar a password em cada pedido e com expiração. Foi
rejeitado neste sprint por exigir emissão, validação e renovação de tokens, e uma
gestão de segredos que não se justificam para o âmbito atual.

### Sessão com formulário de login

Mais próxima do comportamento tradicional de browser, mas obriga a manter estado no
servidor e a ativar proteção CSRF, o que contraria o modelo de API stateless escolhido
no ADR-002.

### OAuth2 / identidade institucional

Seria a opção ideal em produção, mas depende de uma integração externa que não existe
no âmbito do projeto.

## Relação com Outras Decisões

- **ADR-002**: mantém a API REST stateless; a segurança é uma preocupação da camada
  `config`.
- **ADR-003**: `SecurityConfig` em `config`, `UserService` em `service`,
  `AppUserRepository` em `repository`, `AppUser` e `Role` em `domain`.
- **S3-28**: define as permissões por papel nos endpoints existentes.
