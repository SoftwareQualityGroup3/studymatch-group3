# Decisões de Design — Sprint 2

Este documento regista as decisões tomadas no Sprint 2 (*Student Profile & Cold Start*) sobre o histórico académico, as competências, o perfil do estudante, a estratégia de cold start e a funcionalidade própria da equipa. Corresponde ao entregável 12 do enunciado e às secções 2.2 a 2.4.

O Sprint 2 constrói a informação e as regras de negócio que vão suportar o matching. A formação automática de grupos está fora do âmbito deste sprint.

Os conceitos gerais do domínio estão em [`domain-model-concepts.md`](domain-model-concepts.md) e os casos de uso em [`use-cases/`](use-cases/). Este documento descreve o que ficou **efetivamente implementado**; as diferenças face a esses documentos estão na secção 6.

---

## 1. Modelo do histórico académico

**Decisão.** O histórico é um conjunto de registos académicos (`AcademicRecord`). Cada registo liga um estudante a uma unidade curricular (`CourseUnit`) e guarda o ano letivo (formato `YYYY/YY`, por exemplo `2024/25`), o semestre (1 ou 2) e a nota (0 a 20, com duas casas decimais). Cada unidade curricular tem código, nome, ECTS e um peso por competência.

**Porquê.**
- É o mínimo que permite construir um perfil com significado: sabemos *o que* o estudante fez (UC), *quando* (ano e semestre) e *com que resultado* (nota).
- Os pesos ficam na unidade curricular, e não no registo. Assim, o contributo de uma UC para as competências é definido uma só vez e aplica-se a todos os estudantes. Os pesos estão na base de dados (migração `V4`), pelo que se alteram sem mexer no código.
- A validação está no domínio: `AcademicYear` rejeita formatos inválidos (`24/25`, `2024-25`) e anos não consecutivos (`2024/30`); `AcademicRecord` rejeita semestres fora de 1–2 e notas fora de 0–20.
- A base de dados repete as restrições (`CHECK` e `UNIQUE`), como segunda linha de defesa.

---

## 2. Competências e contributo de cada resultado

**Decisão.** O perfil tem quatro competências fixas: `PROGRAMMING`, `MATHEMATICS`, `DATABASES` e `SOFTWARE_ENGINEERING`. Cada unidade curricular tem um peso entre 0 (não contribui) e 1 (contribuição máxima) para cada competência. Uma UC pode contribuir para várias competências; por exemplo, Tecnologias Web pesa 0,8 em programação e 0,2 em engenharia de software.

**Fórmula.** A pontuação académica de uma competência é a média das notas ponderada pelos pesos:

```
pontuação = Σ (nota × peso) / Σ peso
```

A soma percorre os registos do estudante cuja UC tem peso maior que 0 nessa competência. O resultado fica na escala 0–20. O número de evidências (`evidenceCount`) é o número desses registos.

**Porquê.**
- Uma média ponderada é simples de explicar e de verificar à mão.
- Dividir por `Σ peso` (e não pelo número de registos) garante que uma UC com peso baixo não puxa a pontuação para baixo.
- Nenhuma nota é descartada: todas entram no cálculo, com o peso da respetiva UC.

---

## 3. Atualização do perfil e representação da falta de informação

**Atualização.** O perfil **não é guardado**. É recalculado de raiz sempre que é pedido, a partir dos registos académicos e das autoavaliações guardados. Quando se acrescenta uma nota ou uma autoavaliação, o perfil seguinte já a inclui, sem passos de sincronização.

**Falta de informação.** Cada competência tem sempre um resultado com três campos: a pontuação, o número de evidências e a fonte.

| Situação | Pontuação | Evidências | Fonte |
| --- | --- | --- | --- |
| Sem notas e sem autoavaliação | `null` | 0 | `ACADEMIC` |
| Só autoavaliação | derivada da autoavaliação | 0 | `SELF_ASSESSMENT` |
| Só notas | média ponderada | n | `ACADEMIC` |
| Notas e autoavaliação | combinação (secção 4) | n | `MIXED` |

**Porquê.**
- A falta de dados é representada por `null`, e nunca por 0. Um 0 diria que o estudante é mau numa competência, quando o que sabemos é que não sabemos.
- Recalcular evita perfis desatualizados e dispensa uma tabela de perfis: a base de dados guarda apenas factos (notas e autoavaliações), e o perfil é derivado deles.
- O número de evidências e a fonte permitem a quem consome o perfil perceber quanto confiar em cada valor.

---

## 4. Estratégia de cold start

**Decisão.** Quando falta histórico, o estudante preenche uma **autoavaliação** de 1 a 5 em cada competência que quiser (uma por competência, atualizável). A fonte de cold start é uma só: a perceção do próprio estudante.

**Conversão.** O valor 1–5 passa para a escala do perfil (0–20) multiplicando por 4, ou seja, de 4 a 20.

**Transição para evidência académica.** Com `n` evidências académicas numa competência, a autoavaliação conta como **meia evidência**:

```
pesoAcadémico        = n / (n + 0,5)
pesoAutoavaliação    = 1 − pesoAcadémico = 0,5 / (n + 0,5)
pontuação            = pesoAcadémico × pontuaçãoAcadémica + pesoAutoavaliação × autoavaliaçãoConvertida
```

| Evidências (`n`) | Peso das notas | Peso da autoavaliação |
| --- | --- | --- |
| 0 | — (só autoavaliação) | 100 % |
| 1 | 66,7 % | 33,3 % |
| 2 | 80,0 % | 20,0 % |
| 3 | 85,7 % | 14,3 % |
| 10 | 95,2 % | 4,8 % |

**Exemplo.** Um estudante com `PROG1 = 16` e `WEB = 12` tem, em `PROGRAMMING`, a pontuação académica (16×1 + 12×0,8) / 1,8 ≈ 14,22, com `n = 2`. Se a autoavaliação for 5 (convertida para 20), a pontuação final é 0,8 × 14,22 + 0,2 × 20 ≈ 15,38. Numa competência sem notas e com autoavaliação 3, a pontuação é 12 e a fonte é `SELF_ASSESSMENT`.

**Porquê.**
- **Honestidade sobre a origem.** A autoavaliação é informação fraca (subjetiva e não verificada). Por isso conta menos do que qualquer nota e nunca a substitui, o que é coerente com a regra de domínio definida no Sprint 1.
- **Transição automática.** Não há interruptor entre "modo cold start" e "modo normal". A influência da autoavaliação diminui continuamente à medida que as notas se acumulam, e nunca desaparece de forma abrupta.
- **Resposta à pergunta do enunciado.** Mesmo um estudante sem qualquer nota fica com um perfil útil nas competências que preencheu, claramente identificado como vindo de autoavaliação.
- **Simplicidade.** Uma fórmula, uma fonte e uma escala são fáceis de explicar e de testar.

---

## 5. Funcionalidade da equipa: pesquisa e filtro de estudantes

**Decisão.** A lista de estudantes pode ser pesquisada por nome ou número e filtrada por curso e ano curricular.

**Valor.** Com mais do que um punhado de estudantes, a lista completa deixa de ser prática. A pesquisa e o filtro permitem encontrar um estudante e abrir o seu perfil rapidamente, o que será útil quando for preciso escolher quem entra num contexto de agrupamento. Usa campos que o estudante já tem, sem alterar o modelo de dados.

---

## 6. Simplificações face ao Sprint 1

O Sprint 1 definiu um modelo mais rico em [`domain-model-concepts.md`](domain-model-concepts.md) e em [`use-cases/`](use-cases/). Para entregar uma fatia vertical funcional numa semana, o Sprint 2 simplificou o seguinte:

| Sprint 1 (modelo conceptual) | Sprint 2 (implementado) | Consequência |
| --- | --- | --- |
| Inscrições e várias tentativas por UC | Um resultado por UC e por estudante (restrição `UNIQUE`) | Não se representam melhorias de nota nem repetições |
| Competências definidas e mantidas por utilizadores | Quatro competências fixas (`enum`) | Acrescentar uma competência exige alterar o código |
| Estado do perfil (em construção, parcial, completo) e grau de confiança | Sem estado do perfil; usam-se o número de evidências e a fonte | O estado não é explícito, mas pode ser deduzido destes campos |
| Evidências não curriculares de vários tipos | Apenas a autoavaliação | Projetos e certificações ficam para sprints futuros |
| Autoavaliação com menor peso que qualquer evidência | Autoavaliação como meia evidência | A regra mantém-se, agora com um valor concreto |
| Curso, plano de estudos e contexto de curso como entidades | `course` e `curricularYear` como atributos do estudante | Não há histórico de mudanças de curso |
| Estado de conclusão (aprovado, reprovado) | Só a nota; todas as notas entram no cálculo | Uma nota abaixo de 10 também conta como evidência |

**Limitações conhecidas.**
- A autoavaliação mais baixa (1) converte-se em 4 e não em 0, por causa da escala `valor × 4`.
- O número de evidências conta registos, sem considerar o peso. Uma UC com peso 0,2 numa competência conta como uma evidência completa para a transição do cold start, embora o peso dela na média seja baixo.
- Os ECTS são guardados mas não entram no cálculo do perfil.

---

## 7. Testes que sustentam estas decisões

As regras das secções 3 e 4 estão cobertas por testes unitários em `backend/src/test/java/pt/studymatch/`:

- `service/ColdStartCalculatorTest` — conversão da autoavaliação, ausência de dados, só autoavaliação, só notas, combinação, diminuição da influência com mais evidências e rejeição de valores inválidos.
- `service/ProfileCalculatorColdStartTest` — as mesmas regras aplicadas ao perfil completo, incluindo a fonte (`ACADEMIC`, `SELF_ASSESSMENT`, `MIXED`).
- `domain/AcademicRecordTest`, `domain/AcademicYearTest` e `domain/CourseUnitTest` — limites da nota (0 e 20), semestre, formato do ano letivo e limites do peso (0 e 1).
- `service/AcademicRecordServiceTest` — estudante ou UC inexistentes e registo duplicado.
