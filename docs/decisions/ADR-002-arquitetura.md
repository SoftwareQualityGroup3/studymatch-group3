# ADR-002: Arquitetura Inicial da Aplicação

## Estado

Proposto

## Contexto

O StudyMatch é uma aplicação full-stack que deverá evoluir ao longo de vários sprints.

O sistema necessita de integrar:

- um frontend para interação com os utilizadores;
- um backend responsável pela API e pelas regras de negócio;
- persistência de dados;
- comunicação entre frontend e backend;
- pontos de extensão para funcionalidades que serão definidas em sprints futuros.

A stack tecnológica já definida pela equipa inclui:

- React + Vite para o frontend;
- Java 21 + Spring Boot 4.x (4.1.1) para o backend;
- API REST/JSON;
- MySQL 8.4 LTS;
- Spring Data JPA / Hibernate;
- Flyway para migrações da base de dados.

Existem ainda dois problemas de design deliberadamente em aberto no projeto:

- a estratégia de *cold start*, utilizada quando existe pouco ou nenhum histórico académico sobre um estudante;
- a estratégia de *matching*, responsável futuramente pelo processo de formação de grupos.

É, por isso, necessária uma arquitetura com responsabilidades claras, baixo acoplamento entre componentes e capacidade para evoluir sem exigir alterações significativas no restante sistema.

## Decisão

Será adotada uma arquitetura full-stack em camadas.

O frontend será uma SPA desenvolvida com React + Vite e comunicará com o backend através de uma API REST utilizando JSON.

Todos os endpoints da aplicação utilizarão o prefixo:

```text
/api
```

O backend será desenvolvido em Spring Boot e seguirá o fluxo principal:

```text
Controller → Service → Repository
```

As responsabilidades gerais serão distribuídas da seguinte forma:

- **Controller** — recebe os pedidos HTTP, utiliza DTOs para entrada e saída de dados e delega as operações para a camada Service.
- **Service** — contém e coordena as regras de negócio da aplicação.
- **Repository** — realiza o acesso e persistência dos dados através de Spring Data JPA.
- **Domain** — contém as entidades e conceitos centrais do domínio da aplicação.
- **DTO** — representa os dados trocados através da API, evitando expor diretamente o modelo interno.
- **Config** — contém configurações transversais da aplicação, como CORS e Beans do Spring.

O fluxo de dependências deverá respeitar o sentido:

```text
Controller → Service → Repository
```

Um Controller não deverá aceder diretamente a um Repository.

As regras de negócio deverão residir principalmente nos Services e nos objetos de domínio, evitando lógica de negócio nos Controllers.

A persistência será realizada utilizando:

```text
MySQL 8.4 LTS
        ↑
Spring Data JPA / Hibernate
        ↑
Repository
```

Durante o desenvolvimento e os testes de persistência será utilizada uma instância local de MySQL, conforme definido na stack tecnológica do projeto.

As alterações ao esquema da base de dados serão geridas através do Flyway.

As estratégias de *cold start* e *matching* serão tratadas como pontos de extensão substituíveis associados à camada Service.

Nesta fase, a arquitetura apenas prevê esses pontos de extensão, sem definir nem implementar os respetivos algoritmos.

A arquitetura encontra-se representada visualmente em:

```text
docs/images/architecture.png
```

## Regras Arquiteturais

Devem ser respeitadas as seguintes regras:

- o frontend comunica com o backend apenas através da API REST;
- os endpoints utilizam o prefixo `/api`;
- os Controllers não acedem diretamente aos Repositories;
- as regras de negócio não devem ser implementadas nos Controllers;
- a camada Repository é responsável pelo acesso a dados;
- DTOs são utilizados na fronteira da API;
- *cold start* e *matching* devem permanecer desacoplados do restante sistema através de estratégias substituíveis;
- alterações futuras nessas estratégias não deverão exigir alterações significativas nos Controllers ou na persistência.

## Consequências

### Positivas

- separação clara de responsabilidades;
- menor acoplamento entre os componentes;
- maior facilidade de manutenção e evolução do sistema;
- maior facilidade de testar cada camada isoladamente;
- permite alterar as estratégias de *cold start* e *matching* sem reestruturar toda a aplicação;
- facilita a divisão do trabalho entre frontend e backend;
- arquitetura consistente com a stack tecnológica definida pela equipa;
- estrutura adequada à evolução dos requisitos em sprints futuros.

### Negativas / Trade-offs

- introduz mais classes, interfaces e ficheiros do que uma solução sem separação explícita de camadas;
- exige disciplina da equipa para respeitar as dependências entre camadas;
- pode existir algum overhead na transformação entre DTOs, objetos de domínio e entidades persistidas;
- os pontos de extensão previstos para *cold start* e *matching* poderão necessitar de ajustes quando os respetivos requisitos forem definidos com maior detalhe.

## Alternativas Consideradas

### Arquitetura sem separação explícita de camadas

Foi considerada uma solução mais simples onde Controllers poderiam conter regras de negócio e aceder diretamente à persistência.

Esta alternativa foi rejeitada porque aumentaria o acoplamento entre responsabilidades e dificultaria:

- os testes isolados;
- a manutenção;
- a evolução futura do sistema;
- a substituição de estratégias como *cold start* e *matching*.

### GraphQL

Foi considerada a utilização de GraphQL para a comunicação entre frontend e backend.

Foi rejeitada nesta fase porque introduziria complexidade adicional sem uma necessidade identificada no Sprint 1.

REST/JSON foi considerado suficiente para os requisitos atuais e está alinhado com a stack tecnológica aprovada pela equipa.

### Arquitetura orientada a microserviços

Foi considerada a possibilidade de separar diferentes áreas do sistema em serviços independentes.

Esta alternativa foi rejeitada nesta fase porque introduziria complexidade adicional de comunicação, deployment e gestão de infraestrutura sem benefício proporcional para o âmbito atual do StudyMatch.

A arquitetura em camadas permite manter o sistema simples nesta fase e poderá ser revista futuramente caso a dimensão ou os requisitos do projeto o justifiquem.

## Relação com Outras Decisões

Esta decisão está alinhada com a stack tecnológica documentada em:

```text
docs/tech-stack.md
```

A estrutura concreta de pastas e pacotes que materializa esta arquitetura será definida em:

```text
docs/project-structure.md
```

e registada no ADR correspondente à estrutura do projeto.

O diagrama visual desta arquitetura encontra-se em:

```text
docs/images/architecture.png
```