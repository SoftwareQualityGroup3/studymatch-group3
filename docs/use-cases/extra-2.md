# Caso de Uso de Valor Acrescentado #2: Pedir Correção de Dados do Perfil

Este caso de uso propõe uma funcionalidade adicional para o StudyMatch que permite ao estudante sinalizar dados do seu perfil que considera incorretos ou em falta, e aos Serviços Académicos decidir sobre esses pedidos.

A funcionalidade complementa o Perfil do Estudante (UC-PER-1 e UC-PER-2) e a Importação de Histórico Académico (UC-EX1-1). Não altera as respetivas regras de domínio nem define detalhes de implementação.

---

## UC-EX2-1: Pedir Correção de Dados do Perfil

### Nome e Objetivo

**Nome:** Pedir Correção de Dados do Perfil

**Objetivo:** Permitir que o estudante peça a correção de uma informação académica incorreta, desatualizada ou em falta no seu perfil (por exemplo, uma classificação, uma tentativa ou uma unidade curricular), e que essa informação seja revista e, se justificado, corrigida de forma rastreável.

### Ator(es) Primário(s)

- **Estudante** — identifica o problema e submete o pedido de correção.
- **Serviços Académicos** — analisam o pedido e decidem aceitá-lo ou rejeitá-lo.

De acordo com os atores e permissões definidos para o StudyMatch, o estudante tem acesso de leitura aos seus dados e os Serviços Académicos têm permissões de escrita sobre a informação académica. Por isso, o estudante **pede** a correção e não a faz diretamente.

### Cenário de Sucesso Principal

1. O estudante autentica-se na plataforma StudyMatch.
2. O estudante consulta o seu perfil, a trajetória académica ou as evidências de uma competência (UC-PER-1 e UC-PER-2).
3. O estudante identifica uma informação que considera incorreta ou em falta e escolhe pedir a sua correção.
4. O estudante indica o motivo e, quando aplicável, a informação que considera correta.
5. O sistema regista o pedido associado ao estudante e à informação em causa, com o estado **Pendente**, e confirma a submissão.
6. Os Serviços Académicos consultam os pedidos pendentes e analisam o pedido, tendo em conta o registo original e a sua origem (registo manual ou importação).
7. Os Serviços Académicos decidem aceitar ou rejeitar o pedido, indicando a justificação.
8. Se o pedido for aceite, o sistema regista a correção preservando o registo anterior no histórico e atualiza o perfil do estudante.
9. O estudante é informado da decisão e consulta o perfil atualizado ou a justificação da rejeição.

### Fluxos Alternativos / Exceção

**A1 — Informação em falta**

No passo 3, o estudante indica que falta um registo (por exemplo, uma tentativa a uma unidade curricular). O pedido segue o mesmo fluxo e, se aceite, o registo é adicionado ao histórico.

**A2 — Pedido rejeitado**

No passo 7, se o pedido for rejeitado, o estudante vê a justificação e pode submeter um novo pedido com informação adicional.

**A3 — Cancelamento**

O estudante pode cancelar um pedido que ainda esteja **Pendente**.

**A4 — Evidência não curricular submetida pelo estudante**

Se a informação em causa for uma evidência não curricular submetida pelo próprio estudante, aplica-se o processo de validação de evidências já definido para o perfil, e não este fluxo.

**E1 — Já existe um pedido pendente**

Se já existir um pedido pendente sobre a mesma informação, o sistema impede um segundo pedido e mostra o existente.

**E2 — Ator sem permissão**

Se um ator tentar pedir a correção de dados do perfil de outro estudante, o sistema nega a operação.

**E3 — Informação inexistente**

Se a informação indicada já não existir (por exemplo, foi entretanto corrigida), o sistema informa o estudante e não cria o pedido.

### Regras de Negócio e Restrições

- O estudante só pode submeter pedidos sobre o **seu próprio** perfil.
- O estudante não altera diretamente informação académica; apenas pede a correção.
- Apenas os Serviços Académicos decidem sobre pedidos de correção de informação académica.
- Nenhuma informação é eliminada: uma correção aceite cria um novo registo e preserva o anterior, de acordo com as regras de histórico da Trajetória Académica.
- Um pedido tem um estado (**Pendente**, **Aceite**, **Rejeitado** ou **Cancelado**) e todas as mudanças de estado ficam registadas.
- Só pode existir um pedido pendente por cada informação.
- Uma correção aceite reflete-se automaticamente no perfil, sem intervenção do estudante.
- Quando o registo original resultou de uma importação (UC-EX1-1), a correção deve ficar associada a essa origem.

### Conceitos de Domínio Revelados

- **Pedido de Correção** — pedido do estudante sobre uma informação do seu perfil, com motivo, proposta e estado.
- **Decisão de Correção** — resultado da análise, com justificação, autor e data.
- **Estado do Pedido** — Pendente, Aceite, Rejeitado ou Cancelado.
- **Informação Corrigível** — registo académico (por exemplo, uma tentativa) ou evidência a que o pedido se refere.
- **Estudante**, **Serviços Académicos**, **Trajetória Académica**, **Tentativa** e **Evidência** (conceitos já existentes).

---

## Valor Acrescentado para o StudyMatch

- **Qualidade dos dados:** o perfil alimenta, no futuro, a formação de grupos. Um erro numa classificação ou numa tentativa distorce essa base, e o estudante é quem o deteta mais depressa, sobretudo depois de importações em lote (UC-EX1-1).
- **Transparência e confiança:** o estudante vê o que o sistema sabe sobre ele (UC-PER-2) e tem forma de reagir quando discorda.
- **Privacidade:** responde ao direito de retificação dos dados pessoais, previsto no RGPD.
- **Rastreabilidade:** correções e decisões ficam registadas, sem perda de histórico.

### Distinção face a outros casos de uso

| Caso de uso | O que faz | Diferença |
|---|---|---|
| UC-PER-1 e UC-PER-2 | O estudante **consulta** o perfil e as evidências por competência | Este caso de uso começa onde esses terminam: permite **contestar** o que vê |
| UC-EX1-1 | Os Serviços Académicos **importam** dados em lote | Este é individual, iniciado pelo estudante, e trata de erros que a importação não deteta |
| Validação de evidências não curriculares | Valida o que o estudante **submete** | Este trata de dados que o estudante **não** introduziu |

---

## Questões em Aberto e Assunções

- Assume-se que os Serviços Académicos são o único ator que decide. Fica em aberto se o **Docente** deve ser consultado em pedidos sobre classificações das suas unidades curriculares.
- Fica em aberto se o estudante pode anexar provas ou documentos ao pedido.
- Fica em aberto se existe um prazo de resposta e o que acontece se não houver decisão.
- Fica em aberto se os Serviços Académicos podem propor uma alternativa em vez de aceitar ou rejeitar.
- Fica em aberto como se identifica, de forma precisa, a informação em causa (por exemplo, uma tentativa concreta).
- Fica em aberto se o estudante deve poder pedir correções ao contexto de curso (UC-IDE-3), além da informação académica.
- Os detalhes da interface e das notificações ficam fora do âmbito deste caso de uso.
