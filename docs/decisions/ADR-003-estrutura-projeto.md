# ADR-003: Estrutura de Pastas e Pacotes do Projeto

## Estado

Proposto

## Contexto

A arquitetura inicial definida no ADR-002 estabelece uma aplicação full-stack constituída por uma SPA React, uma API REST desenvolvida em Spring Boot e uma camada de persistência baseada em MySQL.

O fluxo principal do backend segue:

```text
Controller → Service → Repository
```

É necessário definir uma estrutura concreta de pastas e pacotes que materialize esta arquitetura e que seja utilizada de forma consistente por todos os membros da equipa.

Sem uma convenção comum, diferentes membros poderiam organizar funcionalidades semelhantes em locais distintos, tornando o projeto mais difícil de compreender, manter, testar e rever.

A estrutura deve indicar de forma clara onde ficam:

- o backend;
- o frontend;
- a documentação;
- os diferentes packages do backend;
- os módulos principais do frontend;
- os recursos e migrações;
- os testes.

---

## Decisão

O repositório será organizado em três áreas principais:

```text
backend/
frontend/
docs/
```

A estrutura acordada para a raiz é:

```text
studymatch-group3/
├── .github/
├── backend/
├── frontend/
├── docs/
├── docker-compose.yml
├── .gitignore
├── CONTRIBUTING.md
└── DevelopmentWorkflow.md
```

Nem todas as pastas ou ficheiros definidos nesta estrutura têm de existir imediatamente.

A estrutura representa a organização acordada para o projeto e será materializada à medida que o esqueleto técnico e as funcionalidades forem implementados.

---

## Backend

O backend utiliza:

- Java 21;
- Spring Boot 4.1.1;
- Maven.

O package base da aplicação é:

```text
pt.studymatch
```

O código principal encontra-se em:

```text
backend/src/main/java/pt/studymatch/
```

A estrutura de packages é:

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

### `controller`

Responsável pela entrada da API REST.

Recebe pedidos HTTP, trabalha com DTOs e delega as operações para a camada `service`.

Não deve conter regras de negócio nem aceder diretamente aos Repositories.

### `service`

Responsável pela lógica de aplicação e pela coordenação das regras de negócio.

Pode utilizar:

- Repositories;
- objetos de domínio;
- futuramente, estratégias substituíveis previstas pela arquitetura.

### `repository`

Responsável pelo acesso e persistência dos dados através de Spring Data JPA.

Não deve conter regras de negócio.

### `domain`

Contém as entidades e conceitos centrais do domínio da aplicação.

### `dto`

Contém os objetos utilizados na transferência de dados entre a API e o exterior.

Evita a exposição direta dos objetos internos através da API REST.

### `config`

Contém configurações transversais da aplicação, como CORS, Beans e outras configurações do Spring.

---

## Regras de Dependência do Backend

O fluxo principal é:

```text
Controller → Service → Repository
```

A camada `Service` pode também utilizar objetos de `Domain`.

São definidas as seguintes regras:

- `controller` pode utilizar `service` e `dto`;
- `controller` não pode aceder diretamente a `repository`;
- `service` pode utilizar `repository` e objetos de `domain`;
- `repository` é responsável pelo acesso e persistência dos dados;
- as regras de negócio devem permanecer principalmente nos Services e nos objetos de domínio;
- DTOs são utilizados na fronteira da API;
- o frontend comunica com o backend apenas através da API REST.

Estas regras materializam as dependências definidas no ADR-002.

---

## Recursos e Persistência

Os recursos do backend encontram-se em:

```text
backend/src/main/resources/
```

O ficheiro de configuração atual encontra-se em:

```text
backend/src/main/resources/application.properties
```

As migrações Flyway serão colocadas em:

```text
backend/src/main/resources/db/migration/
```

A persistência utiliza:

- MySQL 8.4 LTS;
- Spring Data JPA;
- Hibernate;
- Flyway.

A configuração atualmente definida para desenvolvimento e testes de persistência utiliza uma instância local de MySQL 8.4 LTS.

O ficheiro `docker-compose.yml` permanece previsto na estrutura do repositório conforme definido na Issue #25, podendo ser utilizado para serviços que venham a necessitar de execução através de containers.

---

## Frontend

O frontend será desenvolvido utilizando React + Vite.

A estrutura inicial acordada é:

```text
frontend/src/
├── api/
├── components/
├── pages/
├── hooks/
└── assets/
```

### `api`

Contém a comunicação HTTP com a API REST do backend.

### `components`

Contém componentes React reutilizáveis.

### `pages`

Contém os principais ecrãs ou páginas da aplicação.

### `hooks`

Contém hooks React personalizados.

### `assets`

Contém recursos estáticos da aplicação.

A estrutura física do frontend será criada durante a implementação do esqueleto técnico e das funcionalidades correspondentes.

---

## Documentação

A documentação será organizada em:

```text
docs/
├── use-cases/
├── images/
├── decisions/
├── architecture.md
└── project-structure.md
```

### `use-cases/`

Contém a documentação dos casos de uso definidos durante a análise funcional.

### `images/`

Contém diagramas e outras imagens utilizadas na documentação.

### `decisions/`

Contém os Architectural Decision Records utilizados para registar decisões relevantes do projeto.

### `architecture.md`

Documenta a arquitetura geral da aplicação.

### `project-structure.md`

Documenta detalhadamente a estrutura acordada para o repositório.

---

## Testes

### Backend

Os testes do backend ficam em:

```text
backend/src/test/java/pt/studymatch/
```

Atualmente existe:

```text
backend/src/test/java/pt/studymatch/StudyMatchApplicationTest.java
```

À medida que forem criados novos testes, a sua organização deve, sempre que possível, espelhar os packages do código principal.

Exemplo:

```text
pt/studymatch/
├── controller/
├── service/
└── repository/
```

As ferramentas previstas incluem:

- JUnit 5;
- Mockito;
- MockMvc;
- `@SpringBootTest`.

### Frontend

Os testes do frontend utilizarão:

- Vitest;
- React Testing Library.

Os testes poderão ser colocados junto dos módulos que testam utilizando a convenção:

```text
*.test.jsx
```

Exemplo:

```text
StudentCard.jsx
StudentCard.test.jsx
```

---

## Pontos de Extensão

A arquitetura definida no ADR-002 prevê pontos de extensão para funcionalidades cujo comportamento concreto ainda não foi decidido.

Entre estes encontram-se:

- estratégia de *cold start*;
- estratégia de *matching*.

Estas estratégias permanecerão associadas à camada `Service` e deverão ser implementadas de forma substituível.

Nesta fase, a estrutura do projeto não define packages específicos obrigatórios para essas estratégias.

A sua organização concreta será definida quando os respetivos requisitos forem implementados, preservando o desacoplamento definido no ADR-002.

---

## Consequências

### Positivas

- estrutura previsível para todos os membros da equipa;
- alinhamento entre a arquitetura e a organização do código;
- separação clara de responsabilidades;
- maior facilidade de manutenção;
- maior facilidade de localização de código e testes;
- facilita o trabalho paralelo da equipa;
- reduz o risco de mistura de responsabilidades entre camadas;
- permite que a estrutura evolua de forma controlada durante os próximos sprints.

### Negativas / Trade-offs

- exige disciplina da equipa para respeitar a responsabilidade de cada package;
- o crescimento do domínio poderá exigir novos packages ou subpackages;
- uma organização principalmente por camada pode tornar-se menos adequada caso o projeto cresça significativamente;
- a estrutura poderá necessitar de evolução à medida que novos requisitos forem introduzidos.

---

## Alternativas Consideradas

### Organização por funcionalidade

Foi considerada uma organização baseada principalmente nas funcionalidades do sistema.

Exemplo:

```text
feature/
├── student/
├── profile/
├── competency/
└── grouping/
```

Esta organização pode ser útil em aplicações maiores, agrupando num mesmo local os elementos relacionados com uma funcionalidade.

Foi decidido utilizar inicialmente uma organização por camada e responsabilidade porque corresponde diretamente à arquitetura definida no ADR-002 e é adequada à dimensão atual do StudyMatch.

Esta decisão poderá ser revista futuramente se o crescimento ou a complexidade da aplicação o justificar.

### Estrutura sem convenção explícita

Foi considerada a possibilidade de cada membro organizar os ficheiros consoante as necessidades de cada funcionalidade.

Esta alternativa foi rejeitada porque poderia provocar inconsistências na organização do projeto e dificultar:

- manutenção;
- revisão de código;
- localização de componentes;
- localização de testes;
- integração do trabalho realizado pelos diferentes membros da equipa.

---

## Relação com Outras Decisões

Esta decisão concretiza a arquitetura definida em:

```text
docs/architecture.md
```

e no ADR:

```text
docs/decisions/ADR-002-arquitetura.md
```

A descrição detalhada da estrutura encontra-se em:

```text
docs/project-structure.md
```

A stack tecnológica utilizada pelo projeto encontra-se documentada em:

```text
docs/tech-stack.md
```
