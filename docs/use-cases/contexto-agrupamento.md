# Caso de Uso: Contexto de Agrupamento

## UC-GRP-1: Definir Contexto de Agrupamento

### Nome e Objetivo

**Nome:** Definir Contexto de Agrupamento

**Objetivo:** Permitir que um ator autorizado defina o contexto em que um conjunto de grupos de estudantes irá ser formado, especificando a unidade curricular e, quando aplicável, a atividade associada, o tamanho alvo dos grupos e eventuais restrições.

### Ator(es) Primário(s)

- **Docente** — cria e configura contextos de agrupamento associados às unidades curriculares ou atividades pelas quais é responsável.

### Cenário de Sucesso Principal

1. O docente autentica-se na plataforma StudyMatch.
2. O docente acede à secção "Novo Contexto de Agrupamento".
3. O docente associa o contexto a uma unidade curricular pela qual é responsável e, quando aplicável, a uma atividade específica dessa unidade curricular (ex.: trabalho de grupo, projeto ou laboratório).
4. O docente define o tamanho alvo dos grupos, através de um valor fixo ou de um intervalo mínimo/máximo.
5. O docente define, opcionalmente, restrições aplicáveis ao contexto (ex.: número máximo de grupos, prazo limite para formação ou preferência por evitar repetição de colegas de contextos anteriores).
6. O sistema valida os dados introduzidos.
7. O sistema regista o contexto com o estado "aberto" e disponibiliza-o para consulta.

### Fluxos Alternativos / Exceção

**A1 — Contexto sem restrições adicionais**

No passo 5, se o docente não definir restrições adicionais, o sistema regista o contexto utilizando apenas a unidade curricular, a atividade quando aplicável e o tamanho alvo definidos.

**A2 — Edição de um contexto ainda aberto**

Se o docente pretender alterar um contexto já criado, mas ainda no estado "aberto", o sistema permite editar os parâmetros definidos.

**E1 — Tamanho alvo inválido**

Se o tamanho alvo apresentar valores inválidos, por exemplo zero, valores negativos ou tamanho mínimo superior ao tamanho máximo, o sistema rejeita a definição e alerta o docente.

**E2 — Ator sem permissão**

Se um ator sem autorização tentar criar ou alterar um contexto de agrupamento, o sistema nega a operação.

### Regras de Negócio e Restrições

- Um contexto de agrupamento está associado a uma unidade curricular e, quando aplicável, a uma atividade específica dessa unidade curricular.
- O docente apenas pode criar ou alterar contextos associados às unidades curriculares ou atividades pelas quais é responsável.
- O tamanho alvo dos grupos é obrigatório.
- As restantes restrições são opcionais.
- Um contexto só pode ser editado enquanto estiver no estado "aberto".
- Depois de utilizado para a formação de grupos, o contexto passa a ser apenas consultável para efeitos de histórico.
- As restrições definidas no contexto representam condições que deverão ser consideradas futuramente pelo processo de formação de grupos.
- A definição do contexto não inclui nem executa qualquer algoritmo de formação de grupos. Esse processo será tratado em sprints futuros.

### Conceitos de Domínio Revelados

- **Contexto de Agrupamento** — conjunto de parâmetros que define as condições em que os grupos serão futuramente formados.
- **Unidade Curricular** — unidade académica à qual o contexto de agrupamento está associado.
- **Atividade** — tarefa académica específica de uma unidade curricular, como um projeto, trabalho ou laboratório.
- **Tamanho Alvo** — número fixo ou intervalo de estudantes pretendido por grupo.
- **Restrição de Agrupamento** — condição adicional que deverá ser considerada futuramente na formação dos grupos.
- **Estado do Contexto** — indicador do ciclo de vida do contexto, como aberto, em formação ou formado.

---

## UC-GRP-2: Consultar Contexto de Agrupamento

### Nome e Objetivo

**Nome:** Consultar Contexto de Agrupamento

**Objetivo:** Permitir que um estudante ou docente consulte os contextos de agrupamento ativos ou passados relevantes para si, incluindo os respetivos parâmetros.

### Ator(es) Primário(s)

- **Estudante** — consulta os contextos de agrupamento das unidades curriculares ou atividades em que está inscrito.
- **Docente** — consulta os contextos associados às unidades curriculares ou atividades pelas quais é responsável.

### Cenário de Sucesso Principal

1. O ator autentica-se na plataforma StudyMatch.
2. O ator acede à lista de contextos de agrupamento relevantes.
3. O sistema apresenta, para cada contexto, a unidade curricular, a atividade quando aplicável, o tamanho alvo, as restrições definidas e o estado atual.
4. O ator seleciona um contexto.
5. O sistema apresenta os detalhes completos do contexto selecionado.

### Fluxos Alternativos / Exceção

**A1 — Nenhum contexto disponível**

Se não existir nenhum contexto de agrupamento associado ao ator, o sistema apresenta uma mensagem informativa, sem considerar a situação como erro.

**E1 — Ator sem permissão**

Se um ator tentar consultar um contexto associado a uma unidade curricular ou atividade à qual não tem acesso, o sistema nega a consulta.

### Regras de Negócio e Restrições

- O estudante só pode consultar contextos associados às unidades curriculares ou atividades em que está inscrito.
- O docente só pode consultar contextos associados às unidades curriculares ou atividades pelas quais é responsável.
- A consulta é apenas de leitura.
- A consulta do contexto não apresenta nem determina a composição dos grupos.

### Conceitos de Domínio Revelados

- **Contexto de Agrupamento** — conjunto de parâmetros associados à futura formação de grupos.
- **Unidade Curricular** — contexto académico principal ao qual o agrupamento está associado.
- **Atividade** — tarefa académica específica dentro da unidade curricular.
- **Tamanho Alvo** — número fixo ou intervalo pretendido de estudantes por grupo.
- **Restrição de Agrupamento** — condição adicional associada ao contexto.
- **Estado do Contexto** — estado atual do contexto de agrupamento.

---

## Informação de Contexto Definida

Cada contexto de agrupamento guarda, no mínimo:

- **Unidade curricular** — identifica o contexto académico principal em que os grupos serão utilizados.
- **Atividade** — identifica, quando aplicável, a atividade específica para a qual os grupos serão formados.
- **Tamanho alvo** — número fixo ou intervalo mínimo/máximo de estudantes pretendido por grupo.
- **Restrições** — condições adicionais que deverão ser consideradas futuramente pelo processo de formação dos grupos.
- **Estado** — indica a fase atual do contexto e se este ainda pode ser alterado.

As restrições podem incluir, por exemplo, número máximo de grupos, prazo para formação ou preferência por evitar repetição de colegas de contextos anteriores.

Nesta fase não é definido qualquer algoritmo ou critério que determine como os estudantes serão distribuídos pelos grupos. Essa decisão pertence ao mecanismo de matching e fica fora do âmbito deste caso de uso.

---

## Alinhamento com Atores e Permissões

Os atores utilizados neste caso de uso estão alinhados com a definição da issue #7:

- **Docente** — responsável pelas unidades curriculares e atividades onde os grupos serão formados, podendo criar e consultar os respetivos contextos.
- **Estudante** — pode consultar os contextos associados às unidades curriculares e atividades em que está inscrito.
- **Serviços Académicos** — não desempenham, nesta fase, um papel direto na definição ou consulta dos contextos de agrupamento.

---

## Questões em Aberto e Assunções

- Assume-se, nesta fase, que cada contexto está associado a uma única unidade curricular e, quando aplicável, a uma atividade específica dessa unidade curricular.
- Em aberto: determinar se, no futuro, poderão existir contextos associados simultaneamente a várias unidades curriculares, por exemplo em atividades interdisciplinares.
- Em aberto: definir como as restrições de agrupamento serão representadas de forma genérica e extensível, por exemplo através de uma lista estruturada de tipos de restrição.
- Assume-se que o tamanho alvo pode ser representado por um valor fixo ou por um intervalo mínimo/máximo.
- A forma de lidar com situações em que o número de estudantes não permite formar grupos exatamente com o tamanho pretendido será definida futuramente pelo processo de formação de grupos.