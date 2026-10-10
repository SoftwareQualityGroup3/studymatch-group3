# Estrutura do Projeto — StudyMatch

## Objetivo

Este documento define a organização de pastas, pacotes, recursos e testes acordada para o projeto StudyMatch.

A estrutura segue a arquitetura definida em `docs/architecture.md` e procura garantir que todos os membros da equipa organizam o código de forma consistente, respeitando a separação de responsabilidades definida na Issue #18.

---

## Estrutura do Repositório

```text
studymatch-group3/
├── .github/
├── backend/
├── docs/
│   ├── decisions/
│   ├── images/
│   ├── use-cases/
│   ├── architecture.md
│   ├── coding-conventions.md
│   ├── database-setup.md
│   ├── definition-of-done.md
│   ├── domain-model.md
│   ├── project-structure.md
│   └── tech-stack.md
├── frontend/
├── .editorconfig
├── .env.example
├── .gitignore
├── CONTRIBUTING.md
├── DevelopmentWorkflow.md
└── README.md
```

- `.github/` — template utilizado nos Pull Requests;
- `backend/` — aplicação backend em Java 21 com Spring Boot 4.1.1;
- `frontend/` — aplicação frontend em React + Vite;
- `docs/` — documentação do projeto:
  - `decisions/` — Architectural Decision Records (ADRs);
  - `images/` — diagramas e imagens usadas na documentação;
  - `use-cases/` — casos de uso e respetivo índice (`README.md`);
  - `architecture.md` — arquitetura da aplicação;
  - `coding-conventions.md` — convenções de código e nomenclatura;
  - `database-setup.md` — arranque da base de dados MySQL local;
  - `definition-of-done.md` — Definition of Done da Sprint 1;
  - `domain-model.md` — modelo de domínio inicial;
  - `project-structure.md` — este documento;
  - `tech-stack.md` — stack tecnológica adotada;
- `.env.example` — modelo das variáveis de ambiente da base de dados (o `.env` não é versionado);
- `CONTRIBUTING.md` — regras de trabalho da equipa;
- `DevelopmentWorkflow.md` — workflow de desenvolvimento e convenção de branches;
- `README.md` — contexto do produto e instruções de arranque.

---

## Estrutura do Backend

O backend utiliza Maven e segue a estrutura padrão de uma aplicação Spring Boot:

```text
backend/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── pt/
│   │   │       └── studymatch/
│   │   │           ├── config/
│   │   │           ├── controller/
│   │   │           ├── domain/
│   │   │           ├── dto/
│   │   │           ├── repository/
│   │   │           ├── service/
│   │   │           └── StudyMatchApplication.java
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       │       └── V1__init.sql
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── pt/
│               └── studymatch/
│                   ├── controller/
│                   ├── service/
│                   └── StudyMatchApplicationTest.java
├── mvnw
├── mvnw.cmd
└── pom.xml
```

O package base da aplicação é `pt.studymatch` e o código principal encontra-se em `backend/src/main/java/pt/studymatch/`.

### `StudyMatchApplication.java`

Classe principal da aplicação Spring Boot e ponto de entrada do backend.

### `controller/`

Controllers REST, responsáveis pela entrada da API.

- recebem pedidos HTTP;
- validam o formato dos dados recebidos;
- utilizam DTOs para entrada e saída de dados;
- delegam as operações para a camada `service`.

Os Controllers não contêm regras de negócio e não acedem diretamente aos Repositories.

### `service/`

Lógica de aplicação e coordenação das regras de negócio.

- executa os casos de uso;
- interage com os objetos de domínio;
- utiliza Repositories para acesso aos dados;
- integrará futuramente os pontos de extensão *cold start* e *matching*.

### `repository/`

Repositories responsáveis pelo acesso e persistência dos dados, através de Spring Data JPA / Hibernate. Não contêm regras de negócio.

### `domain/`

Entidades e conceitos centrais do domínio do StudyMatch. Os conceitos concretos evoluem de acordo com o modelo de domínio definido pela equipa.

### `dto/`

Data Transfer Objects utilizados na fronteira da API, evitando expor diretamente os objetos internos do domínio.

### `config/`

Configurações transversais da aplicação, como Beans do Spring.

---

## Regras de Dependência do Backend

O fluxo principal definido pela arquitetura é:

```text
Controller → Service → Repository
```

- `controller` pode utilizar `service` e `dto`;
- `controller` não pode aceder diretamente a `repository`;
- `service` pode utilizar `repository` e objetos de `domain`;
- `repository` é responsável apenas pelo acesso e persistência dos dados;
- as regras de negócio ficam nos Services e nos objetos de domínio, nunca nos Controllers;
- DTOs são utilizados na fronteira da API;
- o frontend comunica com o backend através da API REST.

---

## Recursos e Migrações do Backend

Os recursos da aplicação encontram-se em `backend/src/main/resources/`:

- `application.properties` — configuração da aplicação (ligação à BD por variáveis de ambiente);
- `db/migration/` — migrações Flyway, no formato `V<n>__descricao_curta.sql` (ex.: `V1__init.sql`).

---

## Maven e Build do Backend

O ficheiro principal de configuração é `backend/pom.xml`. O Maven Wrapper (`backend/.mvn/`, `backend/mvnw` e `backend/mvnw.cmd`) garante que todos usam a mesma versão do Maven.

---

## Estrutura do Frontend

O frontend é desenvolvido com React + Vite:

```text
frontend/
├── src/
│   ├── api/
│   ├── components/
│   ├── hooks/
│   ├── pages/
│   ├── App.jsx
│   └── main.jsx
├── index.html
├── package.json
└── vite.config.js
```

- `api/` — funções de comunicação HTTP com a API REST do backend (ex.: `health.js`);
- `components/` — componentes React reutilizáveis;
- `pages/` — principais ecrãs da aplicação;
- `hooks/` — hooks React personalizados.

As pastas `components/`, `hooks/` e `pages/` contêm apenas um `.gitkeep` até serem necessárias.

O Vite faz *proxy* dos pedidos `/api` para `http://localhost:8080` (configurado em `vite.config.js`).

---

## Testes

### Backend

Os testes do backend encontram-se em `backend/src/test/java/pt/studymatch/` e a sua estrutura espelha os packages do código principal (`controller/`, `service/`, `repository/`, …).

Ferramentas: JUnit 5, Mockito, MockMvc e `@SpringBootTest`.

### Frontend

Os testes do frontend usam Vitest e React Testing Library. Os ficheiros `*.test.jsx` ficam junto do componente, página, hook ou módulo que testam (ex.: `App.jsx` e `App.test.jsx`).

---

## Consistência com a Arquitetura

```text
React SPA
    ↓
REST / JSON
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL 8.4 LTS (local)
```

As regras de negócio permanecem na camada `Service` e nos objetos de domínio. As estratégias de *cold start* e *matching* são pontos de extensão associados à camada `Service`, sem serem implementadas nesta fase.

A decisão relativa à estrutura do projeto encontra-se registada em `docs/decisions/ADR-003-estrutura-projeto.md`.
