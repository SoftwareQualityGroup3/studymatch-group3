# Convenções de Código e Nomenclatura — StudyMatch

Este documento define as convenções básicas de código, nomenclatura e commits a seguir por toda a equipa, antes do início da implementação do esqueleto técnico. O objetivo é garantir consistência, legibilidade e facilitar a revisão de código entre membros da equipa.

## 1. Idioma

- **Identificadores de código** (classes, métodos, variáveis, pacotes, endpoints, tabelas, etc.) — **inglês**.
- **Documentação** (README, comentários explicativos extensos, mensagens de commit, documentos em `docs/`) — **português**.
- Comentários curtos de código (ex: explicação de uma linha complexa) podem ser em português ou inglês, desde que consistentes dentro do mesmo ficheiro.

## 2. Backend — Java

| Elemento | Convenção | Exemplo |
|---|---|---|
| Pacotes | minúsculas, sem underscores | `com.studymatch.backend.student` |
| Classes / Interfaces | `PascalCase` | `StudentProfile`, `CompetencyService` |
| Métodos | `camelCase` | `getStudentProfile()`, `calculateCompetencyLevel()` |
| Variáveis | `camelCase` | `studentId`, `competencyList` |
| Constantes | `UPPER_SNAKE_CASE` | `MAX_GROUP_SIZE`, `DEFAULT_PAGE_SIZE` |

## 3. Frontend — JavaScript / React

| Elemento | Convenção | Exemplo |
|---|---|---|
| Componentes (e respetivos ficheiros) | `PascalCase` | `StudentProfile.jsx`, `GroupCard.jsx` |
| Funções | `camelCase` | `fetchStudentProfile()`, `handleSubmit()` |
| Variáveis | `camelCase` | `isLoading`, `studentList` |
| Constantes globais | `UPPER_SNAKE_CASE` | `API_BASE_URL` |
| Hooks personalizados | `camelCase`, prefixo `use` | `useStudentProfile()` |

## 4. API REST

- Endpoints em **inglês**, com **substantivos no plural**:
  ```
  GET    /api/students
  GET    /api/students/{id}
  POST   /api/students
  GET    /api/students/{id}/competencies
  GET    /api/groups
  ```
- Usar nomes de recursos, não verbos (`/api/students`, não `/api/getStudents`).
- Hierarquia de recursos reflete relações do domínio (ex: `/api/students/{id}/competencies` para competências de um estudante específico).
- Códigos de estado HTTP semânticos (`200`, `201`, `400`, `404`, etc.) — não devolver sempre `200` com erro no corpo.

## 5. Base de Dados

- Nomes de tabelas e colunas em **`snake_case`**:
  ```
  student
  student_id
  course_unit
  competency_level
  ```
- Tabelas no singular ou plural — **a decidir e manter consistente** (sugestão: singular, ex: `student`, `course_unit`).
- Chaves estrangeiras com sufixo `_id` (ex: `student_id`, `course_unit_id`).
- **Migrações** com o formato:
  ```
  V<n>__descricao_curta.sql
  ```
  Exemplos:
  ```
  V1__create_student_table.sql
  V2__create_competency_table.sql
  V3__add_index_student_email.sql
  ```
  O número `<n>` é sequencial e nunca reutilizado, mesmo que uma migração seja posteriormente corrigida (cria-se uma nova migração).

## 6. Mensagens de Commit

Formato:

```
<tipo>: <descrição curta no imperativo> (#<issue-id>)
```

**Tipos permitidos:**

| Tipo | Quando usar |
|---|---|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `docs` | Alterações de documentação |
| `refactor` | Reestruturação sem alterar comportamento |
| `test` | Adição/alteração de testes |
| `chore` | Configuração, dependências, tarefas de manutenção |

**Exemplos:**

```
feat: adiciona endpoint de health (#22)
fix: corrige validação de email no registo (#31)
docs: documenta convenção de branches (#8)
refactor: extrai lógica de cálculo de competências (#15)
test: adiciona testes unitários ao StudentService (#19)
chore: configura eslint no frontend (#24)
```

**Regras:**

- Descrição curta, no imperativo, em minúsculas, sem ponto final.
- Referência obrigatória à Issue associada, entre parênteses, no final.
- Se o commit fechar a Issue (via Pull Request), usar as keywords do GitHub na descrição do PR (ex: `closes #22`), mantendo a mensagem de commit apenas com a referência `(#22)`.

## 7. Formatação Geral

- Indentação e fim de linha definidos em `.editorconfig` (ver ficheiro na raiz do repositório).
- Sem linhas de código comentadas deixadas no código final (remover antes do merge).
- Ficheiros terminam com uma linha em branco.
- Sem trailing whitespace.

## 8. Estrutura de Pastas (referência)

```
studymatch/
├── frontend/
│   └── src/
│       └── components/
├── backend/
│   └── src/
│       └── main/java/com/studymatch/backend/
├── docs/
│   ├── coding-conventions.md
│   ├── use-cases.md
│   ├── domain-model.md
│   └── architecture.md
├── .editorconfig
├── .gitignore
├── README.md
└── CONTRIBUTING.md
```

---

Estas convenções podem evoluir ao longo do projeto. Qualquer alteração deve ser discutida e documentada novamente neste ficheiro.
