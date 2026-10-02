# Stack Tecnológica — StudyMatch

> **Descrição:** Documentação da stack tecnológica do projeto.  
> **Estado:** Totalmente validado e decidido pela equipa.

## 1. Stack Aprovada e Decidida

| Camada | Tecnologia Validada |
| --- | --- |
| Frontend | React + Vite (JavaScript) |
| Backend | Java 21 (LTS) + Spring Boot 3.x · API REST/JSON em `/api` (Controller → Service → Repository) |
| Persistência | MySQL 8.4 LTS + Spring Data JPA + Flyway |
| Build | Maven com Maven Wrapper (backend) · npm (frontend) |
| Ambiente | MySQL Local (para desenvolvimento e testes de BD) |
| Testes | JUnit 5 + Mockito / MockMvc / `@SpringBootTest` (backend) · Vitest + React Testing Library (frontend) |

## 2. Justificação das Escolhas

As escolhas da stack assentam nos seguintes critérios de avaliação:

- adequação;
- manutenibilidade;
- testabilidade;
- integração;
- conhecimento da equipa;
- tooling.

### 2.1 Frontend — React + Vite (JavaScript)

| Critério | Justificação |
| --- | --- |
| Adequação | React é adequado para gerir o estado reativo da aplicação (perfis, pesquisa, disponibilidades e pedidos de *match*). A utilização de componentes facilita a organização da interface. |
| Manutenibilidade | A interface é dividida em componentes pequenos e reutilizáveis, facilitando a manutenção e evolução da aplicação. |
| Testabilidade | Vitest e React Testing Library permitem testar componentes e comportamentos da interface de forma isolada e próxima da perspetiva do utilizador. |
| Integração | O frontend comunica com a API REST/JSON através de `fetch`. O Vite permite configurar um *proxy* de desenvolvimento para `/api`. |
| Conhecimento da equipa | Validado e aceite pela equipa para o desenvolvimento do frontend. |
| Tooling | Vite fornece um ambiente de desenvolvimento rápido, incluindo *hot reload*. O `npm` gere as dependências e scripts do frontend. |

### 2.2 Backend / API — Java 21 + Spring Boot 3.x

| Critério | Justificação |
| --- | --- |
| Adequação | Java 21 (LTS) e Spring Boot 3.x disponibilizam suporte nativo para desenvolvimento de APIs REST, validação, acesso a dados e integração com os restantes componentes da stack. |
| Manutenibilidade | A arquitetura em camadas `Controller → Service → Repository` separa as responsabilidades da aplicação e facilita a evolução e legibilidade do código. |
| Testabilidade | JUnit 5 e Mockito (incluído no `spring-boot-starter-test`) permitem testar a lógica de negócio de forma isolada. MockMvc permite testar os controllers e `@SpringBootTest` é utilizado para testes de integração. |
| Integração | A API é disponibilizada sob `/api`, utilizando REST e JSON. DTOs e validação permitem separar o contrato da API do modelo de persistência. |
| Conhecimento da equipa | Validado e aprovado pela equipa para a construção do backend. |
| Tooling | Spring Boot integra-se diretamente com Maven e possui excelente suporte nas principais IDEs Java e em pipelines de CI. |

### 2.3 Persistência — MySQL 8.4 LTS + Spring Data JPA + Flyway

| Critério | Justificação |
| --- | --- |
| Adequação | Os dados do StudyMatch (utilizadores, cursos, disponibilidades e *matches*) têm natureza relacional. MySQL 8.4 LTS é a versão estável escolhida com suporte de longo prazo. |
| Manutenibilidade | Spring Data JPA reduz o código repetitivo no acesso a dados. Flyway permite versionar alterações ao esquema de forma declarativa através de migrações SQL no repositório. |
| Testabilidade | Os testes de persistência utilizam uma instância MySQL local dedicada a testes, mantendo total consistência entre o ambiente de testes e o ambiente de execução real. |
| Integração | Spring Data JPA integra-se com Spring Boot e MySQL. O Flyway aplica as migrações automaticamente durante o arranque da aplicação. |
| Conhecimento da equipa | Validado e aceite pela equipa. |
| Tooling | MySQL, Spring Data JPA, Hibernate e Flyway integram-se com o ecossistema Spring Boot e Maven. |

### 2.4 Build — Maven + Maven Wrapper (backend) · npm (frontend)

| Critério | Justificação |
| --- | --- |
| Adequação | Maven foi o ecossistema de build escolhido para o backend. O Maven Wrapper (`./mvnw`) garante que todos utilizem a mesma versão do Maven. O `npm` é essencial no frontend para gerir pacotes e executar scripts. |
| Manutenibilidade | Maven fornece uma estrutura convencional para gerir dependências (`pom.xml`), plugins, compilação e testes no backend. O `package.json` gere o equivalente no frontend. |
| Testabilidade | `./mvnw test` executa os testes do backend. No frontend, os scripts do `package.json` executam o Vitest via `npm test`. |
| Integração | O Maven integra-se com Spring Boot. O `npm` instala o React, Vite, Vitest e restantes módulos de JavaScript. |
| Conhecimento da equipa | Validado e aceite por toda a equipa. |
| Tooling | O Maven Wrapper evita discrepâncias entre ambientes. Maven e npm possuem suporte universal em IDEs e pipelines. |

## 3. Ambiente e Base de Dados

A aplicação utiliza o **MySQL 8.4 LTS** instalado localmente como sistema de gestão de base de dados relacional.

- **Desenvolvimento Local:** A execução e o desenvolvimento da aplicação utilizam uma instância de MySQL local.
- **Base de Dados de Testes:** Os testes de integração de persistência são executados diretamente contra uma instância dedicada do **MySQL local** (ex.: `studymatch_test`), garantindo total consistência com a base de dados real.
- **Gestão de Esquema:** O **Flyway** gere o versionamento de tabelas através de migrações SQL armazenadas no repositório (`src/main/resources/db/migration`).
- **Segurança e Variáveis de Ambiente:** As credenciais de acesso à base de dados (URL, utilizador e password) devem ser configuradas através de variáveis de ambiente (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`), evitando guardar dados sensíveis no repositório.

## 4. Alternativas Consideradas

- **Frontend — Angular:** Rejeitado por introduzir mais complexidade inicial e uma curva de aprendizagem mais íngreme comparado com a abordagem modular do React + Vite.
- **Backend — Quarkus / Micronaut:** Rejeitados a favor da maturidade, documentação e suporte do ecossistema Spring Boot.
- **Migrações — Liquibase:** Rejeitado a favor do **Flyway** devido à simplicidade de escrever migrações diretamente em SQL legível sem necessidade de abstrações em XML/YAML.
- **Testes com BD em Memória (ex: H2):** Rejeitado a favor de **MySQL local** para evitar falsos positivos causados por diferenças de sintaxe e comportamentos entre H2 e MySQL.

## 5. Matriz de Versões Fixadas

| Componente | Versão Aprovada |
| --- | --- |
| Java | 21 (LTS) |
| Spring Boot | 3.x |
| MySQL | **8.4 LTS** |
| Maven | 3.9.x (via Maven Wrapper `./mvnw`) |
| Node.js | Versão LTS |
| React | A fixar no `package.json` |
| Vite | A fixar no `package.json` |
| Vitest | A fixar no `package.json` |

## 6. Estratégia de Testes

### Backend

A estratégia de testes do backend é composta por:

- **JUnit 5:** Framework principal para execução dos testes;
- **Mockito:** Criação de *mocks* e simulação de dependências isoladas nos serviços;
- **MockMvc:** Testes dos controllers REST (validação de payloads JSON, rotas `/api` e códigos HTTP);
- **`@SpringBootTest` / `@DataJpaTest`:** Testes de integração;
- **MySQL Local:** Instância local dedicada para execução de testes que exigem integração real com a base de dados.

### Frontend

A estratégia de testes do frontend é composta por:

- **Vitest:** Test runner rápido e nativo do ecossistema Vite;
- **React Testing Library:** Testes de componentes orientados às interações e comportamentos observáveis do utilizador final.

## 7. Decisões e Validações da Equipa

Todas as tecnologias presentes neste documento foram **discutidas, validadas e aprovadas pela equipa**:

- Java 21 (LTS);
- Spring Boot 3.x;
- API REST/JSON sob a rota `/api`;
- MySQL 8.4 LTS (com suporte local para desenvolvimento e testes);
- Flyway para migrações SQL;
- Spring Data JPA;
- Maven + Maven Wrapper;
- `npm` para gestão de pacotes do frontend;
- React + Vite para o frontend;
- Vitest + React Testing Library para testes do frontend;
- JUnit 5 + Mockito + MockMvc + `@SpringBootTest` para testes do backend.

## 8. Entregável

Este documento deve ser guardado no repositório em:

```text
docs/tech-stack.md
```