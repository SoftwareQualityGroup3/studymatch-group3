# Sistema de Competências

Este documento analisa como as competências são definidas e associadas a unidades curriculares ou a outra evidência académica, de acordo com a área de capacidade "Competências" do Sprint 1. Não são tomadas decisões de implementação — apenas modelação do domínio e do comportamento esperado.

---

## UC-COM-1 — Definir Competência

**Nome e objetivo:** Permitir que um ator autorizado crie e descreva uma nova competência no sistema, para que esta possa posteriormente ser associada a unidades curriculares ou outras evidências.

**Ator(es) primário(s):** Coordenador de curso / Docente responsável *(ator com permissões administrativas sobre o catálogo de competências — o nome exato do papel fica em aberto, ver Questões em Aberto)*.

**Cenário de sucesso principal:**
1. O ator acede à área de gestão de competências.
2. O ator introduz o nome, descrição e, opcionalmente, a categoria da competência (ex: técnica, transversal).
3. O sistema valida que não existe já uma competência com o mesmo nome.
4. O sistema regista a competência no catálogo, disponível para associação futura.

**Fluxos alternativos/exceção:**
- **A1 — Competência duplicada:** se já existir uma competência com nome igual ou muito semelhante, o sistema alerta o ator e sugere reutilizar a existente em vez de criar uma nova.
- **E1 — Competência sem nenhuma associação futura:** uma competência criada mas nunca associada a evidências fica visível apenas no catálogo administrativo, não nos perfis dos estudantes.

**Regras de negócio e restrições:**
- O nome da competência é único no catálogo.
- Uma competência só é relevante para os perfis de estudantes depois de ter pelo menos uma associação a uma unidade curricular ou outra evidência (ver UC-COM-2).

**Conceitos de domínio revelados:** Competência, Categoria de Competência, Catálogo de Competências.

---

## UC-COM-2 — Associar Competência a Unidade Curricular

**Nome e objetivo:** Permitir associar uma ou mais competências a uma unidade curricular, definindo o grau de contribuição dessa unidade curricular para cada competência.

**Ator(es) primário(s):** Coordenador de curso / Docente responsável.

**Cenário de sucesso principal:**
1. O ator seleciona uma unidade curricular existente.
2. O ator seleciona uma ou mais competências do catálogo a associar.
3. Para cada associação, o ator indica o grau/peso de contribuição (ver Modelo de Competências).
4. O sistema regista a associação, tornando-a efetiva para todos os estudantes que concluam essa unidade curricular a partir desse momento.

**Fluxos alternativos/exceção:**
- **A1 — Alteração de associação existente:** se a unidade curricular já tiver competências associadas, o ator pode ajustar os pesos; o sistema regista a alteração com data, sem apagar o histórico de perfis já calculados anteriormente.
- **E1 — Unidade curricular sem competências associadas:** é permitido (ex: unidades muito recentes ou em análise), mas o sistema sinaliza isso como uma lacuna na área de gestão.

**Regras de negócio e restrições:**
- Uma unidade curricular pode contribuir para múltiplas competências.
- Uma competência pode ser alimentada por múltiplas unidades curriculares.
- O peso de contribuição é relativo à unidade curricular (ex: "Estruturas de Dados" contribui fortemente para "Resolução de Problemas", fracamente para "Comunicação").

**Conceitos de domínio revelados:** Unidade Curricular, Associação Competência–Unidade Curricular, Peso/Grau de Contribuição.

---

## UC-COM-3 — Associar Competência a Evidência Não Curricular

**Nome e objetivo:** Permitir que uma competência seja alimentada por evidências que não resultam diretamente de uma unidade curricular (ex: participação em projetos, atividades extracurriculares, autoavaliação), relevante sobretudo em cenários de cold start.

**Ator(es) primário(s):** Estudante (submete evidência); Coordenador de curso / Docente (valida, quando aplicável).

**Cenário de sucesso principal:**
1. O estudante regista uma evidência não curricular (ex: participação num projeto, certificação externa).
2. O estudante associa essa evidência a uma ou mais competências do catálogo.
3. O sistema marca a evidência como "não validada" até revisão (se o modelo de validação assim o exigir).
4. A evidência passa a contribuir para o cálculo da competência correspondente no perfil do estudante.

**Fluxos alternativos/exceção:**
- **A1 — Evidência sujeita a validação:** se a política da equipa exigir validação humana, a evidência só contribui para o perfil depois de aprovada por um docente/coordenador.
- **E1 — Evidência não verificável:** o sistema permite o registo, mas atribui-lhe um grau de confiança mais baixo do que a uma evidência curricular.

**Regras de negócio e restrições:**
- Evidências não curriculares têm, por defeito, menor peso do que evidências curriculares, salvo validação explícita.
- Um estudante pode associar a mesma evidência a mais do que uma competência, se justificável.

**Conceitos de domínio revelados:** Evidência, Evidência Curricular vs. Não Curricular, Grau de Confiança, Validação de Evidência.

---

## Modelo de Competências (proposta inicial)

> As decisões abaixo são propostas de arranque, sujeitas a evolução nos sprints seguintes. Não incluem fórmulas definitivas.

**Definição:** uma competência é um conceito avaliável (ex: "Trabalho em Equipa", "Pensamento Analítico"), associado a uma ou mais fontes de evidência, com um nível calculado a partir dessas evidências.

**Relações:**
- `Competência` 1 — N `Associação` N — 1 `Unidade Curricular` (relação muitos-para-muitos com atributo de peso).
- `Competência` 1 — N `Evidência Não Curricular`.
- `Competência` pode ter uma `Categoria` (ex: técnica, transversal) — opcional.

**Escala (proposta):** escala ordinal de 4 níveis — *Iniciante, Em Desenvolvimento, Consolidado, Avançado* — em vez de uma escala numérica contínua, por ser mais interpretável no contexto académico e menos sujeita a falsa precisão nas fases iniciais do projeto.

**Regras de contribuição (proposta):**
- Cada associação (unidade curricular ou evidência) contribui com um peso relativo (ex: 1–3, baixo/médio/alto).
- O nível de competência resulta da combinação do número de evidências, do seu peso e da classificação obtida (quando aplicável), sem uma fórmula fechada nesta fase — a definir e refinar em sprint posterior, com base em dados reais.
- Evidências mais recentes ou mais diretamente relacionadas pesam mais do que evidências antigas ou indiretas (princípio geral, não fórmula).

## Quem Define e Mantém as Competências

O catálogo de competências e as suas associações a unidades curriculares são geridos por um ator com papel administrativo/académico (Coordenador de curso ou Docente responsável). Estudantes podem **submeter** evidência não curricular, mas não podem criar ou alterar competências no catálogo nem os pesos das associações curriculares.

## Questões em Aberto / Assunções

- Que papel/ator concreto tem autoridade para gerir o catálogo? (assume-se "Coordenador de curso", a confirmar com o contexto real da instituição.)
- As evidências não curriculares precisam sempre de validação humana, ou pode haver auto-validação para certos tipos (ex: certificações externas verificáveis)?
- Como lidar com competências "órfãs" (sem associações) — devem ser visíveis no catálogo administrativo apenas, ou removidas automaticamente?
- A escala de 4 níveis é suficiente, ou alguns contextos de agrupamento vão precisar de maior granularidade?
- Como é tratada a evolução de uma competência ao longo do tempo (ex: regressão se o estudante não usar a competência há muito tempo)? Fica em aberto para sprints futuros.
