# Caso de Uso: Trajetória Académica

## UC-TRA-1: Consultar Trajetória Académica

### Nome e Objetivo

**Nome:** Consultar Trajetória Académica

**Objetivo:** Permitir que um estudante (ou outro ator autorizado) visualize o histórico de unidades curriculares, inscrições, tentativas, classificações e progressão no curso ao longo dos anos letivos.

### Ator(es) Primário(s)

- **Estudante** — consulta a sua própria trajetória.
- **Docente / Serviços Académicos** *(ator secundário, se aplicável)* — pode consultar a trajetória de estudantes inscritos numa unidade curricular ou curso, com permissões restritas.

> Lista de atores e permissões a confirmar após decisão da equipa na issue #7.

### Cenário de Sucesso Principal

1. O estudante autentica-se na plataforma StudyMatch.
2. O estudante acede à secção "Trajetória Académica".
3. O sistema recolhe as inscrições e tentativas do estudante em todas as unidades curriculares, organizadas por ano letivo.
4. Para cada unidade curricular, o sistema apresenta o estado atual (aprovado / reprovado / em curso), a classificação final e o número de tentativas realizadas.
5. O sistema apresenta a progressão global no curso (ex.: unidades concluídas vs. total do plano de estudos).
6. O estudante visualiza a trajetória apresentada, organizada cronologicamente por ano letivo.

### Fluxos Alternativos / Exceção

**A1 — Unidade curricular com múltiplas tentativas**

No passo 4, se existir mais de uma tentativa para a mesma unidade curricular (reprovação seguida de repetição), o sistema apresenta todas as tentativas, identificando claramente qual delas é válida para efeitos de conclusão.

**A2 — Mudança de ano letivo ou plano de estudos**

Se o estudante tiver tentativas registadas sob planos de estudos ou anos letivos diferentes, o sistema mantém o histórico de ambos e sinaliza a transição, sem eliminar dados anteriores.

**E1 — Ator sem permissão**

Se um ator tentar consultar a trajetória de um estudante sem autorização, o sistema nega o acesso e regista a tentativa.

**E2 — Falha ao obter dados**

Se o sistema não conseguir obter dados de uma fonte (ex.: serviço de classificações indisponível), apresenta a trajetória com os dados disponíveis e um aviso de informação incompleta.

### Regras de Negócio e Restrições

- O estudante tem acesso apenas de leitura à sua própria trajetória académica.
- Outros atores apenas podem consultar a trajetória de um estudante se possuírem autorização para o efeito.
- A trajetória académica é organizada cronologicamente por ano letivo.
- Todas as tentativas realizadas numa unidade curricular devem permanecer disponíveis no histórico, mesmo quando existe uma tentativa posterior com aprovação.
- Uma nova tentativa nunca elimina ou substitui o registo de uma tentativa anterior.
- O estado atual de uma unidade curricular e a progressão no curso são determinados a partir da informação existente no histórico académico.
- Alterações de ano letivo ou plano de estudos não eliminam informação histórica anterior.

### Conceitos de Domínio Revelados

- **Trajetória Académica** — conjunto cronológico das inscrições, tentativas e resultados de um estudante ao longo do curso.
- **Unidade Curricular** — unidade de ensino pertencente ao plano de estudos do estudante.
- **Inscrição** — associação entre um estudante e uma unidade curricular num determinado ano letivo.
- **Tentativa** — ocorrência individual de uma inscrição ou avaliação numa unidade curricular.
- **Ano Letivo** — período temporal utilizado para organizar inscrições e tentativas.
- **Classificação** — resultado atribuído a uma tentativa realizada pelo estudante.
- **Estado de Conclusão** — estado atual de uma unidade curricular, como aprovado, reprovado ou em curso.
- **Progressão no Curso** — medida do avanço do estudante relativamente ao plano de estudos.

---

## UC-TRA-2: Registar Resultado de Unidade Curricular

### Nome e Objetivo

**Nome:** Registar Resultado de Unidade Curricular

**Objetivo:** Permitir que uma nova tentativa (inscrição, exame, época) de uma unidade curricular seja registada na trajetória do estudante, incluindo casos de repetição.

### Ator(es) Primário(s)

- **Docente / Serviços Académicos** — regista ou atualiza o resultado de uma unidade curricular.

### Cenário de Sucesso Principal

1. O ator autorizado seleciona o estudante e a unidade curricular.
2. O sistema verifica se já existem tentativas anteriores para essa unidade curricular.
3. O ator regista a nova tentativa: ano letivo, época de avaliação, classificação e estado (aprovado/reprovado).
4. O sistema associa a tentativa ao número sequencial correto (1.ª, 2.ª, 3.ª tentativa, etc.).
5. O sistema atualiza a trajetória do estudante, mantendo o histórico de tentativas anteriores.

### Fluxos Alternativos / Exceção

**A1 — Repetição de unidade curricular**

Se já existir uma tentativa anterior reprovada, o sistema associa a nova tentativa à mesma unidade curricular, incrementando o contador de tentativas, sem substituir o registo anterior.

**E1 — Registo duplicado**

Se já existir um registo para a mesma unidade curricular, ano letivo e época, o sistema rejeita o registo e alerta o ator.

### Regras de Negócio e Restrições

- Cada tentativa é um registo distinto e imutável; uma nova tentativa nunca apaga uma anterior.
- O estado "concluído" de uma unidade curricular é determinado pela tentativa mais recente com aprovação, se existir alguma.
- Uma unidade curricular pode ter zero, uma ou várias tentativas ao longo de diferentes anos letivos.
- Apenas atores autorizados podem registar ou alterar tentativas; o estudante tem acesso apenas de leitura à sua trajetória.

### Conceitos de Domínio Revelados

- **Unidade Curricular** — entidade base do percurso académico, associada a um plano de estudos e ano letivo.
- **Inscrição** — associação entre estudante e unidade curricular num determinado ano letivo.
- **Tentativa** — registo individual de uma inscrição/avaliação numa época específica, com classificação e estado.
- **Ano Letivo** — período temporal que enquadra inscrições e tentativas.
- **Classificação** — valor numérico ou qualitativo atribuído a uma tentativa.
- **Estado de Conclusão** — indicador (aprovado/reprovado/em curso) derivado das tentativas de uma unidade curricular.
- **Trajetória Académica** — agregação cronológica de todas as inscrições e tentativas de um estudante.
- **Progressão no Curso** — medida do avanço do estudante face ao plano de estudos total.

---

## Evidência Histórica Armazenada

**O quê:** por cada unidade curricular, são armazenadas todas as tentativas realizadas, e não apenas a última. Cada tentativa contém:

- ano letivo;
- época de avaliação;
- classificação;
- estado (aprovado/reprovado);
- número sequencial da tentativa.

**Granularidade:** a informação é armazenada ao nível da tentativa individual, e não apenas do resultado final da unidade curricular. Isto permite distinguir reprovações de aprovações posteriores e identificar repetições.

**Organização:** a informação é agrupada por: estudante → unidade curricular → lista cronológica de tentativas.

As tentativas são ordenadas por ano letivo e época de avaliação. A trajetória atual é uma vista derivada deste histórico, não um campo separado que substitui informação anterior.

---

## Reprovações, Repetições e Anos Letivos

- Uma reprovação não é substituída nem apagada quando o estudante repete a unidade curricular.
- Cada repetição gera uma nova tentativa associada ao ano letivo em que ocorreu.
- O histórico permite reconstruir a evolução do estudante ao longo do tempo.
- Mudanças de plano de estudos ou de ano letivo não eliminam tentativas anteriores.
- O sistema deve conseguir representar trajetórias que atravessem mais do que um plano de estudos ou ano letivo.

---

## Questões em Aberto e Assunções

- Assume-se que a lista final de atores e permissões da issue #7 confirmará **Estudante** e **Docente / Serviços Académicos** como atores relevantes para esta área. Esta definição deverá ser ajustada caso a equipa decida atores diferentes.
- Em aberto: como tratar creditações ou equivalências de unidades curriculares realizadas fora do curso atual, por exemplo numa transferência de outra instituição. Este tema fica fora do âmbito do Sprint 1.
- Em aberto: determinar se avaliações contínuas, com várias classificações parciais antes da classificação final, deverão ser representadas como sub-registos de uma tentativa ou se será guardada apenas a classificação final. Assume-se, nesta fase, apenas a classificação final.
- Assume-se que a progressão no curso é calculada com base nas unidades curriculares ou ECTS concluídos relativamente ao total do plano de estudos, sem definir nesta fase o cálculo exato.