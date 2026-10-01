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
5. O sistema apresenta a progressão global no curso (ex: unidades concluídas vs. total do plano de estudos).
6. O estudante visualiza a trajetória apresentada, organizada cronologicamente por ano letivo.

### Fluxos Alternativos / Exceção

**A1 — Unidade curricular com múltiplas tentativas**

No passo 4, se existir mais de uma tentativa para a mesma unidade curricular (reprovação seguida de repetição), o sistema apresenta todas as tentativas, identificando claramente qual delas é a válida para efeitos de conclusão (normalmente a mais recente com aprovação).

**A2 — Mudança de ano letivo ou plano de estudos**

Se o estudante tiver tentativas registadas sob planos de estudos ou anos letivos diferentes, o sistema mantém o histórico de ambos e sinaliza a transição, sem eliminar dados anteriores.

**E1 — Ator sem permissão**

Se um ator tentar consultar a trajetória de um estudante sem autorização, o sistema nega o acesso e regista a tentativa.

**E2 — Falha ao obter dados**

Se o sistema não conseguir obter dados de uma fonte (ex: serviço de classificações indisponível), apresenta a trajetória com os dados disponíveis e um aviso de informação incompleta.

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
4. O sistema associa a tentativa ao número sequencial correto (1ª, 2ª, 3ª tentativa, etc.).
5. O sistema atualiza a trajetória do estudante, mantendo o histórico de tentativas anteriores.

### Fluxos Alternativos / Exceção

**A1 — Repetição de unidade curricular**

Se já existir uma tentativa anterior reprovada, o sistema associa a nova tentativa à mesma unidade curricular, incrementando o contador de tentativas, sem substituir o registo anterior.

**E1 — Registo duplicado**

Se já existir um registo para a mesma unidade curricular, ano letivo e época, o sistema rejeita o registo e alerta o ator.

### Regras de Negócio e Restrições

- Cada tentativa é um registo distinto e imutável; uma nova tentativa nunca apaga uma anterior.
- O estado "concluído" de uma unidade curricular é determinado pela tentativa mais recente com aprovação (se existir alguma).
- Uma unidade curricular pode ter zero, uma ou várias tentativas ao longo de diferentes anos letivos.
- Apenas atores autorizados podem registar/alterar tentativas; o estudante tem acesso apenas de leitura à sua trajetória.

### Conceitos de Domínio Revelados

- **Unidade Curricular** — entidade base do percurso académico, associada a um plano de estudos e ano letivo.
- **Inscrição** — associação entre estudante e unidade curricular num determinado ano letivo.
- **Tentativa** — registo individual de uma inscrição/avaliação numa época específica, com classificação e estado.
- **Ano Letivo** — período temporal que enquadra inscrições e tentativas.
- **Classificação** — valor numérico ou qualitativo atribuído a uma tentativa.
- **Estado de Conclusão** — indicador (aprovado/reprovado/em curso) derivado das tentativas de uma unidade curricular.
- **Trajetória Académica** — agregação cronológica de todas as inscrições e tentativas de um estudante.
- **Progressão no Curso** — medida do avanço do estudante face ao plano de estudos total.

## Evidência Histórica Armazenada

**O quê:** por cada unidade curricular, todas as tentativas realizadas (não só a última), cada uma com: ano letivo, época de avaliação, classificação, estado (aprovado/reprovado) e número sequencial da tentativa.

**Granularidade:** ao nível da tentativa individual, não apenas do resultado final da unidade curricular — é isto que permite distinguir reprovações de aprovações posteriores e contar repetições.

**Organização:** agrupada por estudante → unidade curricular → lista cronológica de tentativas (ordenadas por ano letivo/época). A trajetória "atual" é uma vista derivada desta lista, não um campo guardado à parte.

## Reprovações, Repetições e Anos Letivos

- Uma reprovação não é substituída nem apagada quando o estudante repete a unidade curricular — fica registada como uma tentativa anterior no histórico.
- Cada repetição gera uma nova tentativa associada ao ano letivo em que ocorreu, permitindo reconstruir a evolução do estudante ao longo do tempo.
- Mudanças de plano de estudos ou de ano letivo não eliminam tentativas anteriores; o sistema deve conseguir representar trajetórias que atravessam mais do que um plano/ano.

## Questões em Aberto e Assunções

- Assume-se que a lista final de atores e permissões (issue #7) confirmará "Estudante" e "Docente/Serviços Académicos" como atores relevantes para esta área — a decompor/ajustar se a equipa definir atores diferentes.
- Em aberto: como tratar creditações/equivalências de unidades curriculares feitas fora do curso atual (ex: transferência de outra instituição) — ficam fora do escopo do Sprint 1.
- Em aberto: se avaliações contínuas (várias notas parciais antes da nota final) devem ser modeladas como sub-registos de uma tentativa ou apenas como a classificação final da tentativa — assume-se, para já, apenas a classificação final.
- Assume-se que "progressão no curso" é calculada como proporção de ECTS/unidades concluídas face ao total do plano de estudos, sem detalhar aqui o cálculo exato (decisão de domínio, não de implementação).