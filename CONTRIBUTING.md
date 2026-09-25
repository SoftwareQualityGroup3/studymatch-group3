# Contributing — StudyMatch

Este documento define as regras de trabalho da equipa para o desenvolvimento do projeto StudyMatch (Software Quality, Sprint 1).

## Fluxo de Desenvolvimento

```
Issue → Branch → Implementação → Pull Request → Review → Merge
```

Todo o trabalho relevante deve estar associado a uma Issue. Desenvolvimento direto na `main` não é permitido.

A branch criada para cada Issue deve seguir a convenção de nomenclatura definida em [#5 — Documentar convenção de branching](https://github.com/SoftwareQualityGroup3/studymatch-group3/issues/5).
g
## Regras de Pull Request

- Título claro e descritivo do que foi feito.
- A descrição deve incluir `Closes #N`, referenciando a Issue que a PR resolve.
- A PR deve ser pequena e focada — idealmente resolve uma única Issue/objetivo.

## Regras de Revisão

- Mínimo de **1 aprovação** antes do merge.
- Ninguém pode aprovar a própria PR.
- Revisor atribuído por rotação: **Guilherme → João → Alexandre → Ricardo → Guilherme** (ciclo).
    - Se o revisor da vez estiver indisponível, a revisão passa para o próximo elemento da rotação (substituto).
- Todos os comentários da review devem estar **resolvidos** antes do merge.

## Fecho da Pull Request

- Método de merge acordado pela equipa: **Squash and merge** (mantém o histórico da `main` limpo, um commit por Issue).
- `Automatically delete head branches` deve estar ativado no repositório, para que a branch seja apagada automaticamente após o merge.

## Proteção da Branch `main`

- Pull Request obrigatória para qualquer alteração — sem push direto.
- Mínimo de 1 aprovação obrigatória antes do merge.
