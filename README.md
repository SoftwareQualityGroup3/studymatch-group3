# StudyMatch

**Formação adaptativa de grupos de estudantes.**

Projeto da unidade curricular de Qualidade de Software (Sprint 1: fundação do projeto).

## Contexto do produto

Ao longo de um curso, os estudantes trabalham repetidamente em grupo. Na maioria dos casos, os grupos são formados ao acaso, por amizade ou por disponibilidade, o que não aproveita os conhecimentos, a experiência e a evolução que cada estudante acumula durante o percurso académico.

O **StudyMatch** é uma aplicação *full-stack* que pretende apoiar a formação automática de grupos a partir da informação acumulada ao longo do curso. Para isso, o sistema constrói uma representação de cada estudante (percurso académico, competências e outras evidências) e usa-a para formar grupos em diferentes contextos académicos.

Duas questões de desenho ficam intencionalmente em aberto e são propostas e justificadas pela equipa:

- **Cold start:** como representar um estudante com pouco ou nenhum histórico académico.
- **Matching:** como usar o histórico, as competências e outras evidências para formar grupos à medida que os estudantes progridem.

## Objetivo do Sprint 1

Estabelecer uma base profissional sobre a qual o produto possa evoluir sem recomeçar quando os requisitos mudarem:

- organização do projeto e fluxo de trabalho em equipa no GitHub;
- escolha e justificação da stack tecnológica;
- exploração do problema através de casos de uso;
- modelo de domínio e arquitetura iniciais;
- esqueleto técnico *full-stack* executável (frontend, backend e base de dados);
- práticas iniciais de qualidade de software.

## Equipa

| Membro |
| --- |
| Alexandre |
| Guilherme |
| João |
| Ricardo |

## Stack tecnológica

| Camada | Tecnologia |
| --- | --- |
| Frontend | React + Vite (JavaScript) |
| Backend | Java 21 (LTS) + Spring Boot 4.1.1 · API REST/JSON em `/api` |
| Persistência | MySQL 8.4 LTS + Spring Data JPA + Flyway |
| Build | Maven com Maven Wrapper (backend) · npm (frontend) |
| Ambiente | MySQL local (desenvolvimento e testes de BD) |
| Testes | JUnit 5, Mockito, MockMvc e `@SpringBootTest` (backend) · Vitest e React Testing Library (frontend) |

A justificação das escolhas e as versões fixadas estão em [`docs/tech-stack.md`](docs/tech-stack.md).

## Estrutura do repositório

```
studymatch/
├── backend/     # Aplicação Java + Spring Boot (pacote base pt.studymatch)
├── frontend/    # Aplicação React + Vite
├── docs/        # Documentação do projeto
├── docker-compose.yml   # MySQL em Docker (alternativa ao MySQL local)
└── .env.example
```

O detalhe dos pacotes e das pastas está em [`docs/project-structure.md`](docs/project-structure.md).

## Documentação

| Documento | Conteúdo |
| --- | --- |
| [`docs/use-cases/`](docs/use-cases/) | Casos de uso por área |
| [`docs/architecture.md`](docs/architecture.md) | Arquitetura da aplicação |
| [`docs/tech-stack.md`](docs/tech-stack.md) | Tecnologias escolhidas e justificação |
| [`docs/project-structure.md`](docs/project-structure.md) | Estrutura de pastas e pacotes |
| [`docs/coding-conventions.md`](docs/coding-conventions.md) | Convenções de código e de commits |
| [`docs/definition-of-done.md`](docs/definition-of-done.md) | Definition of Done do Sprint 1 |
| [`docs/database-setup.md`](docs/database-setup.md) | Como arrancar a base de dados |
| [`docs/decisions/`](docs/decisions/) | Decisões de arquitetura (ADRs) |

## Como contribuir

O fluxo de trabalho é **Issue → Branch → Pull Request → Review → Merge**. Não se desenvolve diretamente na `main`.

- Cada PR resolve uma Issue (`Closes #N`), tem pelo menos **1 aprovação** de quem não é o autor, e todos os comentários resolvidos antes do merge.
- O merge faz-se com **Squash and merge** e a branch é apagada depois.
- Regras completas: [`CONTRIBUTING.md`](CONTRIBUTING.md). Convenção de branches: [`DevelopmentWorkflow.md`](DevelopmentWorkflow.md).
- Planeamento e tarefas: [GitHub Project board](https://github.com/orgs/SoftwareQualityGroup3/projects) *(substituir pela ligação exata do board "StudyMatch Sprint 1")*

## Como executar

Os pré-requisitos e os passos para arrancar a base de dados, o backend e o frontend serão acrescentados na versão final deste README (#29). Até lá, para a base de dados, ver [`docs/database-setup.md`](docs/database-setup.md).
