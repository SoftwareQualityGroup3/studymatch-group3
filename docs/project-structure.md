# Estrutura do Projeto — StudyMatch

## Objetivo

Este documento define a organização de pastas, pacotes, recursos e testes acordada para o projeto StudyMatch.

A estrutura segue a arquitetura definida em `docs/architecture.md` e procura garantir que todos os membros da equipa organizam o código de forma consistente, respeitando a separação de responsabilidades definida na Issue #18.

---

## Estrutura do Repositório

Atualmente, o repositório encontra-se organizado da seguinte forma:

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
│   ├── project-structure.md
│   └── tech-stack.md
├── .editorConfig
├── .gitignore
├── CONTRIBUTING.md
└── DevelopmentWorkflow.md
```

A estrutura acordada para o projeto prevê também a criação de:

```text
studymatch-group3/
├── frontend/
└── docker-compose.yml
```

Nem todas as pastas ou ficheiros definidos na estrutura acordada têm de existir desde o início do projeto. Alguns serão criados à medida que o esqueleto técnico e as funcionalidades forem implementados.

### `.github/`

Contém configurações específicas do GitHub, incluindo o template utilizado nos Pull Requests.

### `backend/`

Contém a aplicação backend desenvolvida em Java 21 com Spring Boot 4.1.1.

### `frontend/`

Contém a aplicação frontend desenvolvida em React + Vite.

Esta pasta ainda não se encontra materializada no repositório atual e será criada durante a implementação do esqueleto técnico do frontend.

### `docs/`

Contém a documentação do projeto.

Atualmente encontra-se organizada da seguinte forma:

```text
docs/
├── decisions/
├── images/
├── use-cases/
├── architecture.md
├── coding-conventions.md
├── project-structure.md
└── tech-stack.md
```

- `decisions/` — Architectural Decision Records (ADRs);
- `images/` — diagramas e outras imagens utilizadas na documentação;
- `use-cases/` — documentação dos casos de uso;
- `architecture.md` — descrição da arquitetura da aplicação;
- `coding-conventions.md` — convenções de código e nomenclatura;
- `project-structure.md` — descrição da estrutura de pastas e pacotes;
- `tech-stack.md` — stack tecnológica adotada pelo projeto.

### `docker-compose.yml`

O ficheiro `docker-compose.yml` está previsto na estrutura acordada para o projeto, embora ainda não faça parte da estrutura atual do repositório.

A configuração aprovada atualmente para desenvolvimento e testes de persistência utiliza uma instância local de MySQL 8.4 LTS.

A eventual utilização futura de Docker deverá permanecer alinhada com as decisões tecnológicas e arquiteturais da equipa.

### `CONTRIBUTING.md`

Contém orientações para contribuir para o projeto.

### `DevelopmentWorkflow.md`

Documenta o workflow de desenvolvimento e colaboração adotado pela equipa.

---

## Estrutura do Backend

O backend já existente utiliza Maven e segue a estrutura padrão de uma aplicação Spring Boot.

A estrutura atual é:

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
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── pt/
│               └── studymatch/
│                   └── StudyMatchApplicationTest.java
├── mvnw
├── mvnw.cmd
└── pom.xml
```

O package base da aplicação é:

```text
pt.studymatch
```

O código principal encontra-se em:

```text
backend/src/main/java/pt/studymatch/
```

---

## Packages do Backend

A organização principal do backend é:

```text
pt/studymatch/
├── config/
├── controller/
├── domain/
├── dto/
├── repository/
├── service/
└── StudyMatchApplication.java
```

### `StudyMatchApplication.java`

Contém a classe principal da aplicação Spring Boot e constitui o ponto de entrada do backend.

### `controller/`

Contém os Controllers REST responsáveis pela entrada da API.

Responsabilidades:

- receber pedidos HTTP;
- validar o formato dos dados recebidos;
- utilizar DTOs para entrada e saída de dados;
- delegar as operações para a camada `service`.

Os Controllers não devem conter regras de negócio e não devem aceder diretamente aos Repositories.

### `service/`

Contém a lógica de aplicação e coordena as regras de negócio.

Responsabilidades:

- executar os casos de uso;
- aplicar e coordenar regras de negócio;
- interagir com os objetos de domínio;
- utilizar Repositories para acesso aos dados;
- integrar futuramente pontos de extensão como *cold start* e *matching*.

### `repository/`

Contém os Repositories responsáveis pelo acesso e persistência dos dados.

A persistência é realizada através de Spring Data JPA / Hibernate.

Esta camada não deve conter regras de negócio.

### `domain/`

Contém as entidades e conceitos centrais do domínio do StudyMatch.

Os conceitos concretos deste package irão evoluir de acordo com o modelo de domínio definido pela equipa ao longo do projeto.

### `dto/`

Contém os Data Transfer Objects utilizados na fronteira da API.

Os DTOs representam os dados recebidos e devolvidos através da API REST, evitando expor diretamente os objetos internos do domínio.

### `config/`

Contém configurações transversais da aplicação.

Pode incluir, por exemplo:

- configuração de CORS;
- Beans do Spring;
- outras configurações partilhadas pela aplicação.

---

## Regras de Dependência do Backend

O fluxo principal definido pela arquitetura é:

```text
Controller → Service → Repository
```

A camada `Service` pode também utilizar objetos do `Domain`.

Devem ser respeitadas as seguintes regras:

- `controller` pode utilizar `service` e `dto`;
- `controller` não pode aceder diretamente a `repository`;
- `service` pode utilizar `repository` e objetos de `domain`;
- `repository` é responsável pelo acesso e persistência dos dados;
- as regras de negócio devem permanecer principalmente nos Services e nos objetos de domínio;
- as regras de negócio não devem ser colocadas nos Controllers;
- DTOs devem ser utilizados na fronteira da API;
- o frontend comunica com o backend através da API REST.

---

## Recursos do Backend

Os recursos da aplicação encontram-se em:

```text
backend/src/main/resources/
```

Atualmente existe:

```text
backend/src/main/resources/application.properties
```

As migrações Flyway deverão ser colocadas em:

```text
backend/src/main/resources/db/migration/
```

Exemplo:

```text
resources/
├── application.properties
└── db/
    └── migration/
        ├── V1__initial_schema.sql
        └── V2__add_student.sql
```

A pasta `db/migration/` será criada quando forem introduzidas as primeiras migrações de base de dados.

---

## Maven e Build do Backend

O backend utiliza Maven.

O ficheiro principal de configuração é:

```text
backend/pom.xml
```

O Maven Wrapper está disponível através de:

```text
backend/.mvn/
backend/mvnw
backend/mvnw.cmd
```

Isto permite utilizar o Maven Wrapper para executar os comandos de build de forma consistente entre os diferentes membros da equipa.

---

## Estrutura do Frontend

O frontend será desenvolvido utilizando React + Vite.

O código principal ficará em:

```text
frontend/src/
```

A estrutura inicial acordada é:

```text
frontend/
└── src/
    ├── api/
    ├── components/
    ├── pages/
    ├── hooks/
    └── assets/
```

### `api/`

Contém funções responsáveis pela comunicação HTTP com a API REST do backend.

Exemplo:

```text
api/
└── studentApi.js
```

### `components/`

Contém componentes React reutilizáveis.

Exemplo:

```text
components/
├── Header.jsx
├── Loading.jsx
└── ErrorMessage.jsx
```

### `pages/`

Contém os principais ecrãs ou páginas da aplicação.

Exemplo:

```text
pages/
├── HomePage.jsx
└── StudentProfilePage.jsx
```

### `hooks/`

Contém hooks React personalizados reutilizáveis entre diferentes componentes ou páginas.

### `assets/`

Contém recursos estáticos da aplicação, como imagens, ícones e estilos.

A estrutura física do frontend será criada durante a implementação do respetivo esqueleto técnico.

---

## Testes do Backend

Os testes do backend encontram-se em:

```text
backend/src/test/java/pt/studymatch/
```

Atualmente existe o teste base:

```text
StudyMatchApplicationTest.java
```

À medida que forem introduzidos testes para cada camada, a sua estrutura deverá, sempre que possível, espelhar os packages utilizados no código principal.

Exemplo:

```text
backend/src/test/java/pt/studymatch/
├── controller/
├── service/
└── repository/
```

As ferramentas previstas para os testes do backend são:

- JUnit 5;
- Mockito;
- MockMvc;
- `@SpringBootTest`;
- outros mecanismos de teste do Spring quando aplicável.

---

## Testes do Frontend

Os testes do frontend utilizarão Vitest e React Testing Library.

Os ficheiros de teste podem ficar junto do componente, página, hook ou módulo que testam.

Exemplo:

```text
components/
├── StudentCard.jsx
└── StudentCard.test.jsx
```

A mesma organização pode ser utilizada em:

```text
pages/
hooks/
api/
```

---

## Consistência com a Arquitetura

Esta estrutura concretiza a arquitetura definida na Issue #18 e documentada em:

```text
docs/architecture.md
```

O fluxo principal da aplicação mantém-se:

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
MySQL 8.4 LTS
```

A configuração atualmente definida utiliza uma instância local de MySQL 8.4 LTS.

As regras de negócio permanecem principalmente na camada `Service` e nos objetos de domínio.

Os Controllers não acedem diretamente aos Repositories.

As estratégias de *cold start* e *matching* permanecem como pontos de extensão associados à camada `Service`, sem serem implementadas nesta fase.

A decisão relativa à estrutura do projeto encontra-se registada em:

```text
docs/decisions/ADR-003-estrutura-projeto.md
```
