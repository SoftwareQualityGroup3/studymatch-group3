## Development Workflow

### Branching Convention

Para manter o histórico do repositório organizado e rastreável, todas as branches seguem o padrão abaixo.

**Formato geral**

```
<tipo>/<issue-id>-<descrição-curta>
```

- **tipo** — natureza do trabalho (ver tabela)
- **issue-id** — número da Issue do GitHub associada (obrigatório)
- **descrição-curta** — 2 a 5 palavras, minúsculas, separadas por hífen (`-`)

**Tipos de branch**

| Tipo | Quando usar | Exemplo |
|------|-------------|---------|
| `feature/` | Nova funcionalidade | `feature/12-perfil-do-estudante` |
| `fix/` | Correção de bug | `fix/27-erro-login-token` |
| `docs/` | Alterações de documentação | `docs/8-convencao-branches` |

**Regras**

1. Todas as branches derivam da `main`.
2. Toda a branch está associada a uma Issue — não há desenvolvimento sem Issue correspondente.
3. Nomes em minúsculas, palavras separadas por hífen.
4. Sem desenvolvimento direto na `main` — todo o trabalho passa por Pull Request.
5. Um membro da equipa não aprova o próprio Pull Request.
6. Após o merge, a branch é apagada (local e remotamente).

**Exemplos completos**

```
feature/12-perfil-do-estudante
feature/14-formacao-de-grupos
fix/27-erro-login-token
docs/8-convencao-branches
```

**Fluxo de desenvolvimento**

```
Issue → Branch → Implementação → Pull Request → Review → Merge
```

1. Criar/atribuir a Issue no GitHub Project board.
2. Criar a branch a partir da `main`, seguindo a convenção acima.
3. Desenvolver com commits claros, referenciando a Issue (ex: `refs #12`).
4. Abrir Pull Request para `main`, referenciando a Issue (`closes #12`).
5. Pelo menos um membro da equipa (diferente do autor) faz a review.
6. Após aprovação, merge e eliminação da branch.
