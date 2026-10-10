# Cold Start: Estudante sem (ou com pouco) Histórico Académico

Este documento analisa o que acontece quando um estudante tem pouco ou nenhum histórico académico. O enunciado deixa a estratégia em aberto, pelo que se propõe e justifica uma estratégia inicial.

Os casos de uso mantêm-se ao nível funcional: não definem fórmulas, algoritmos nem detalhes de implementação.

---

## 1. Problema

O perfil do estudante (UC-PER-1 e UC-PER-2) é construído a partir de evidências curriculares e não curriculares. Um estudante no 1.º semestre, ou que ingressa sem histórico, tem poucas ou nenhumas evidências. O sistema precisa de o representar **sem inventar informação**, mantendo a regra já definida de que uma competência só aparece no perfil quando existe pelo menos uma evidência associada.

O caso de uso UC-IDE-1 (A1) deixa o tratamento de *cold start* fora do seu âmbito e remete-o para esta área.

## 2. Atores

- **Estudante** — fornece informação inicial e acompanha a evolução do seu perfil.
- **Serviços Académicos** — podem introduzir histórico anterior do estudante através da importação (UC-EX1-1).
- **Sistema StudyMatch** — determina o estado do perfil e atualiza-o quando surgem novas evidências.

---

## 3. Casos de Uso

### UC-CST-1: Consultar Estado do Perfil e Próximos Passos

**Objetivo:** mostrar ao estudante, de forma honesta, o grau de completude do seu perfil e o que pode fazer para o melhorar. Estende o fluxo A1 de UC-PER-1 (perfil com pouca informação).

**Ator primário:** Estudante.

**Cenário de sucesso principal**
1. O estudante autentica-se e acede ao seu perfil.
2. O sistema avalia a quantidade e a diversidade das evidências disponíveis.
3. O sistema indica o estado do perfil (por exemplo, **em construção**, **parcial** ou **completo**).
4. O sistema apresenta o contexto de curso (UC-IDE-2) e, apenas para as competências com evidência, o respetivo nível e grau de confiança.
5. O sistema sugere ações para enriquecer o perfil: preencher a autoavaliação inicial (UC-CST-2), submeter evidências não curriculares ou, quando existe histórico anterior, pedir aos Serviços Académicos a sua importação (UC-EX1-1).

**Fluxos alternativos / exceção**
- **A1:** sem qualquer evidência, o perfil mostra apenas o contexto de curso e as sugestões, sem competências nem níveis.
- **A2:** se o estudante tiver histórico importado, o perfil passa logo a "parcial" e mostra as competências sustentadas por esse histórico.
- **E1:** se uma fonte de dados estiver indisponível, o sistema mostra o que existe e avisa que a informação pode estar incompleta.

**Regras de negócio**
- Nunca se atribuem níveis a competências sem evidência.
- Uma competência com evidência insuficiente pode ser apresentada sem nível definitivo.
- O estado do perfil é visível para quem o consulta.

### UC-CST-2: Registar Autoavaliação Inicial

**Objetivo:** permitir ao estudante fornecer evidência inicial sobre as suas competências, quando ainda não tem histórico.

**Ator primário:** Estudante.

**Cenário de sucesso principal**
1. O estudante escolhe completar o seu perfil.
2. O sistema apresenta um questionário curto sobre competências do catálogo relevantes para o seu curso.
3. O estudante indica a perceção do seu nível em cada competência que quiser, e pode ignorar as restantes.
4. O sistema regista as respostas como **autoavaliação**, uma evidência não curricular de peso reduzido.
5. O perfil é atualizado e mostra essas competências com o respetivo grau de confiança.

**Fluxos alternativos / exceção**
- **A1:** respostas parciais ficam guardadas e o estudante retoma mais tarde.
- **A2:** o estudante altera as respostas; as versões anteriores ficam no histórico.
- **E1:** se a gravação falhar, as respostas mantêm-se no ecrã e o estudante pode tentar de novo.

**Regras de negócio**
- A autoavaliação é opcional.
- O estudante só avalia competências já existentes no catálogo; não cria nem altera competências.
- A autoavaliação tem menor peso do que qualquer evidência curricular ou validada, e nunca a substitui ou elimina.
- A autoavaliação não exige validação, mas é identificada como tal no perfil.

### UC-CST-3: Atualizar o Perfil com Novas Evidências

**Objetivo:** garantir que, à medida que o estudante progride, as evidências académicas ganham peso sobre a informação inicial e o perfil evolui sem ação manual.

**Ator primário:** Sistema (desencadeado por novas evidências).

**Cenário de sucesso principal**
1. Surge uma nova evidência: um resultado registado, um histórico importado (UC-EX1-1), uma correção aceite (UC-EX2-1) ou uma evidência não curricular validada.
2. O sistema associa-a às competências relevantes.
3. O sistema atualiza o grau de confiança e, quando houver evidência suficiente, o nível de cada competência afetada.
4. Onde existe evidência curricular ou validada, esta pesa mais do que a autoavaliação inicial.
5. O sistema atualiza o estado do perfil e o estudante vê-o refletido na próxima consulta.

**Fluxos alternativos / exceção**
- **A1:** se a nova evidência contradiz a autoavaliação, ambas ficam registadas e o perfil reflete as regras de peso, sem penalizar o estudante.
- **E1:** se a unidade curricular não tiver competências associadas, a evidência fica registada e nenhuma competência é atualizada até a associação existir.

**Regras de negócio**
- A atualização é automática.
- Nenhuma evidência anterior é eliminada; o histórico mantém-se.

---

## 4. Estratégia Proposta

**Perfil progressivo baseado em evidências, com a autoavaliação como evidência inicial de menor peso e o histórico anterior como a fonte mais fiável.**

Fontes de informação, da mais para a menos fiável:

1. Evidências curriculares: resultados e tentativas da trajetória académica, incluindo histórico importado (UC-EX1-1).
2. Evidências não curriculares validadas (projetos, certificações, atividades).
3. Evidências não curriculares ainda não validadas.
4. Autoavaliação inicial.

O contexto de curso (curso, plano de estudos, ano curricular) é usado como **contexto**, não como prova de competência.

### Alternativas consideradas

| Alternativa | Vantagens | Desvantagens | Decisão |
|---|---|---|---|
| **A. Perfil neutro** (nível médio para todos) | Simples; todos têm perfil | Viola a regra de não haver níveis sem evidência; esconde diferenças reais | Rejeitada |
| **B. Aguardar histórico** (sem grupos até haver dados) | Nunca usa dados duvidosos | Exclui os primeiros anos do valor do produto; adia o problema | Rejeitada |
| **C. Autoavaliação inicial** | Informação imediata; envolve o estudante | Subjetiva, sujeita a sobre ou subestimação | **Escolhida**, com peso reduzido |
| **D. Importação de histórico anterior** (UC-EX1-1) | Mais objetiva; evita registo manual | Depende dos Serviços Académicos e de dados disponíveis | **Escolhida**, como fonte principal quando existir |
| **E. Inferir pelo plano de estudos** | Sem esforço do estudante | Presume o que o estudante ainda não demonstrou | Só como contexto |

**Justificação:** C e D respeitam a regra de não inventar níveis, dão valor desde o primeiro dia e tornam a incerteza visível através do grau de confiança. A progressão automática (UC-CST-3) faz o *cold start* desaparecer à medida que o histórico cresce.

## 5. Comportamento com Histórico Parcial

| Situação | Estado do perfil | Comportamento |
|---|---|---|
| Sem evidências | Em construção | Só contexto de curso e sugestões |
| Só autoavaliação | Parcial (confiança baixa) | Competências com peso reduzido, identificadas como autoavaliação |
| Histórico importado ou algumas UCs concluídas | Parcial | Competências sustentadas por resultados com maior confiança; as restantes mantêm a autoavaliação |
| Várias UCs, atividades e evidências validadas | Completo | A evidência curricular domina; a autoavaliação torna-se secundária |

A transição é gradual e contínua, sem salto nem ação manual.

## 6. Conceitos de Domínio Revelados

- **Estado do Perfil** — indicador de completude (em construção, parcial, completo); a confirmar se é um conceito ou apenas uma visão derivada.
- **Autoavaliação** — nova modalidade de evidência não curricular, de peso reduzido e sem validação.
- **Questionário de Autoavaliação** — conjunto de competências a que o estudante responde.
- **Grau de Confiança**, **Evidência Curricular**, **Evidência Não Curricular** e **Validação de Evidência** — conceitos já definidos no perfil.

## 7. Assunções

- Existe um catálogo de competências e as associações entre competências e unidades curriculares (UC-COM).
- O estudante pode preencher a autoavaliação no primeiro acesso ou mais tarde.
- A autoavaliação é um tipo de evidência não curricular, o que implica atualizar essa classificação no modelo de domínio.

## 8. Questões em Aberto

- Quais os critérios para os estados "em construção", "parcial" e "completo"?
- Como se pondera a autoavaliação face às restantes evidências (matching, sprints futuros)?
- A autoavaliação deve ser reconfirmada periodicamente?
- O docente ou os Serviços Académicos devem ver a autoavaliação de um estudante, ou apenas evidências curriculares e validadas?
- Como tratar estudantes que ingressam com equivalências ou creditações, que o perfil deixa fora do Sprint 1?
- A importação (UC-EX1-1) deve poder criar também estudantes novos, como aponta o `extra-1.md`?
