# Definition of Done — StudyMatch (Sprint 1)

Este documento define o que significa "terminado" para uma Issue do Sprint 1. Uma Issue só é considerada concluída quando cumpre todos os critérios aplicáveis abaixo.

Complementa as regras de equipa em [`CONTRIBUTING.md`](../CONTRIBUTING.md) e a convenção de branches em [`DevelopmentWorkflow.md`](../DevelopmentWorkflow.md), e é referenciado pela checklist do template de Pull Request (`.github/PULL_REQUEST_TEMPLATE.md`).

## Que critérios se aplicam?

| Tipo de trabalho | Critérios aplicáveis |
| --- | --- |
| Só documentação | 1 (comuns) + 2 (documentação) |
| Só código | 1 (comuns) + 3 (código) |
| Código e documentação | 1 + 2 + 3 |

---

## 1. Critérios Comuns

Aplicam-se a todas as Issues e Pull Requests.

- [ ] Os critérios de aceitação da Issue (passos e entregável) estão cumpridos.
- [ ] A branch foi criada a partir da `main` com o nome `<tipo>/<issue-id>-<descrição-curta>` (ex.: `docs/26-definition-of-done`), conforme `DevelopmentWorkflow.md`.
- [ ] Os commits seguem o formato `<tipo>: <descrição curta no imperativo> (#N)`, conforme `docs/coding-conventions.md`.
- [ ] A Pull Request é dirigida à `main`, é pequena e focada num único objetivo, e tem o template preenchido.
- [ ] A descrição da PR inclui `Closes #N`, referenciando a Issue que resolve.
- [ ] A PR foi revista e aprovada por, pelo menos, **1 membro que não é o autor**.
- [ ] O revisor segue a rotação **Guilherme → João → Alexandre → Ricardo → Guilherme**; se estiver indisponível, passa ao elemento seguinte.
- [ ] Todos os comentários da review estão **resolvidos**.
- [ ] O merge é feito com **Squash and merge**, sem push direto na `main`.
- [ ] A branch foi apagada após o merge (automaticamente, com `Automatically delete head branches`, e também localmente).
- [ ] A Issue está em **Done** no board do projeto *StudyMatch Sprint 1*.

---

## 2. Critérios para Documentação

Aplicam-se a ficheiros em `docs/` (documentos, ADRs, casos de uso e diagramas).

- [ ] O ficheiro está no caminho definido em `docs/`: ADRs em `docs/decisions/` (`ADR-NNN-titulo.md`), casos de uso em `docs/use-cases/` e imagens em `docs/images/`.
- [ ] O texto está escrito em **português**, conforme `docs/coding-conventions.md`.
- [ ] Não há contradições com os restantes documentos, em particular `docs/architecture.md`, `docs/tech-stack.md`, `docs/coding-conventions.md`, `docs/project-structure.md` e os ADRs existentes. Em especial, as versões e tecnologias mencionadas coincidem com a stack aprovada (Java 21, Spring Boot 4.1.1, MySQL 8.4 LTS local).
- [ ] Se a alteração afeta outros documentos, estes foram atualizados na mesma PR (ou a PR indica o que fica por atualizar).
- [ ] Quando o documento regista uma decisão relevante, existe um ADR com estado, contexto, decisão, consequências e alternativas consideradas.
- [ ] Os casos de uso descrevem o comportamento esperado sem decisões de implementação (endpoints, tabelas, ecrãs).
- [ ] O Markdown renderiza corretamente no GitHub, e os links, referências a ficheiros e imagens funcionam.

---

## 3. Critérios para Código

Aplicam-se a alterações em `backend/` e `frontend/`.

### Build e testes

- [ ] **Backend:** `./mvnw clean verify` (executado em `backend/`) termina sem erros.
- [ ] **Frontend:** `npm run build` (executado em `frontend/`) termina sem erros e `npm test` passa.
- [ ] Os testes passam e existem testes novos ou atualizados para o código alterado: JUnit 5, Mockito, MockMvc e `@SpringBootTest` no backend; Vitest e React Testing Library no frontend.
- [ ] Os testes que usam base de dados correm contra o MySQL local de testes (`DB_NAME=studymatch_test`), com as variáveis de ambiente configuradas.

### Convenções e arquitetura

- [ ] Cumpre as convenções de código e nomenclatura de `docs/coding-conventions.md` (identificadores em inglês, formato das migrações `V<n>__descricao_curta.sql`, etc.).
- [ ] Respeita a arquitetura em camadas `Controller → Service → Repository`: os Controllers não acedem diretamente a Repositories nem contêm regras de negócio, e os DTOs são usados na fronteira da API.
- [ ] As alterações ao esquema da base de dados são feitas por migrações Flyway, e nunca pelo Hibernate (`ddl-auto=validate`).
- [ ] A formatação respeita o `.editorConfig`.

### Limpeza e segurança

- [ ] Não há segredos commitados: as credenciais são lidas de variáveis de ambiente (ex.: `DB_USER`, `DB_PASSWORD`) e o `.env` não é versionado.
- [ ] Não há `console.log`, prints de debug nem código comentado esquecido.

> O frontend ainda não existe no repositório. Os critérios de `npm` aplicam-se a partir do momento em que a pasta `frontend/` for criada.

---

## 4. Evolução do Documento

Esta Definition of Done é a do Sprint 1 e pode evoluir nos sprints seguintes. Qualquer alteração deve ser discutida pela equipa e registada neste ficheiro.
