# Cold Start: Estudante sem (ou com pouco) Histórico Académico

Este documento descreve o comportamento esperado do StudyMatch quando um estudante tem pouco ou nenhum histórico académico. A estratégia deixa de depender da autoavaliação de 1–5 e passa a utilizar testes de nível por competência.

As regras de cálculo, os pesos, os limites de tempo e as decisões de segurança estão definidos em [`../decisions/ADR-004-cold-start.md`](../decisions/ADR-004-cold-start.md). Os casos de uso descrevem o comportamento funcional, sem definir endpoints, tabelas ou classes de implementação.

---

## 1. Problema

A autoavaliação de 1–5 utilizada no Sprint 2 dependia da perceção do próprio estudante. Essa perceção pode ser subjetiva, pode ser sobrestimada e não garante que estudantes diferentes interpretem a escala da mesma forma. Além disso, a conversão por multiplicação por 4 fazia com que o valor mais baixo correspondesse a 4/20, o que inflacionava estruturalmente o resultado.

Por outro lado, esperar pelas primeiras notas académicas deixa estudantes sem histórico sem informação útil sobre as suas competências.

O StudyMatch passa, por isso, a recolher evidência através de testes de nível com dificuldade crescente e tempo limitado. O objetivo é produzir uma estimativa comparável, sem apresentar como conhecido um nível para o qual não existem evidências.

## 2. Atores

- **Estudante** — realiza testes de nível e consulta o perfil atualizado.
- **Serviços Académicos** — podem introduzir histórico académico anterior através da importação (UC-EX1-1).
- **Sistema StudyMatch** — apresenta os testes, regista as respostas, calcula os resultados e atualiza o perfil.

## 3. Competências abrangidas

Os testes de nível abrangem quatro competências:

1. **Programação**
2. **Matemática**
3. **Bases de Dados**
4. **Engenharia de Software**

Cada competência tem um teste e um resultado próprios. O estudante pode realizar os testes separadamente.

## 4. Casos de Uso

### UC-CST-1: Consultar Perfil de Competências e Próximos Passos

**Objetivo:** apresentar o nível conhecido de cada competência, a origem da informação, o grau de confiança e as ações disponíveis para melhorar o perfil.

**Ator primário:** Estudante.

**Cenário de sucesso principal**

1. O estudante acede ao seu perfil.
2. O sistema consulta as evidências académicas e os resultados dos testes de nível concluídos.
3. Para cada competência, o sistema determina o nível e o grau de confiança de acordo com a estratégia definida no ADR-004.
4. O sistema apresenta a origem do nível: notas académicas, teste de nível ou combinação de ambos.
5. Quando uma competência não tem notas relevantes nem teste concluído, o sistema não inventa um nível e indica que o estudante pode realizar ou retomar o respetivo teste.
6. O estudante consulta os resultados e identifica as competências com informação em falta ou confiança reduzida.

**Fluxos alternativos / exceção**

- **A1 — Sem evidências:** o nível da competência é apresentado como indisponível, e não como zero; o sistema sugere realizar ou retomar o teste correspondente.
- **A2 — Só existem notas:** o nível baseia-se na média académica da competência.
- **A3 — Só existe teste concluído:** o nível baseia-se no resultado do teste.
- **A4 — Existem notas e teste concluído:** o sistema combina as duas fontes segundo o ADR-004.
- **E1 — Informação indisponível:** o sistema informa o estudante e não apresenta um resultado inventado.

**Regras de negócio**

- Não se atribui um nível quando não existe qualquer evidência.
- A ausência de informação não equivale a uma pontuação de zero.
- A origem do nível e o grau de confiança devem ser apresentados de forma transparente.
- Os resultados de testes iniciados mas não concluídos não são usados no cálculo do perfil.

### UC-CST-2: Realizar Teste de Nível por Competência

**Objetivo:** obter evidência inicial e comparável do nível do estudante numa das quatro competências.

**Ator primário:** Estudante.

**Pré-condições**

- O estudante está identificado no sistema.
- O teste da competência escolhida ainda não foi concluído.

**Cenário de sucesso principal**

1. O estudante escolhe uma competência: Programação, Matemática, Bases de Dados ou Engenharia de Software.
2. O sistema apresenta as regras do teste, incluindo o limite de tempo, a progressão de dificuldade, a opção «Não sei», a ausência de desconto por respostas erradas, o carácter único e definitivo da tentativa e a impossibilidade de voltar a perguntas anteriores.
3. O sistema apresenta uma pergunta ou problema de cada vez, começando pelo nível mais baixo e aumentando a dificuldade de forma ordenada.
4. Em Matemática, Bases de Dados e Engenharia de Software, o estudante responde a 20 perguntas de escolha múltipla, uma por nível. Existe uma única resposta correta entre quatro opções, além da opção «Não sei».
5. Em Programação, o estudante resolve quatro problemas Java de dificuldade crescente. O código é avaliado através de testes.
6. O estudante responde, escolhe «Não sei» ou deixa terminar o tempo disponível.
7. Depois de cada item, o sistema avança para o seguinte. Não é possível regressar a um item anterior nem alterar uma resposta submetida.
8. Quando todos os itens terminam, o sistema calcula e regista o resultado final na escala de 0–20.
9. O estudante consulta o resultado definitivo, que passa a ser usado no perfil da competência.

**Fluxos alternativos / exceção**

- **A1 — «Não sei»:** o item vale zero e o sistema avança para o seguinte.
- **A2 — Tempo esgotado:** o item conta como não respondido, vale zero e o sistema avança.
- **A3 — Sessão interrompida:** o estudante pode retomar a mesma sessão; as respostas já submetidas e os tempos registados não são reiniciados.
- **A4 — Teste já concluído:** o sistema apresenta o resultado existente e não permite iniciar uma nova tentativa.
- **E1 — Executor de Programação indisponível:** o sistema informa que o teste de Programação está temporariamente indisponível e não inventa uma pontuação; a falha não consome a tentativa única, pelo que o estudante pode realizar o teste quando o executor estiver novamente disponível.
- **E2 — Falha durante a gravação:** o sistema informa o estudante e preserva a sessão de forma a permitir a retoma, sem criar uma segunda tentativa.

**Regras de negócio**

- Existe um teste por competência.
- Cada teste pode ser concluído apenas uma vez; o resultado final é definitivo.
- Uma sessão em curso pode ser retomada, mas não reiniciada.
- A dificuldade aumenta por ordem de nível, independentemente das respostas anteriores.
- Em Matemática, Bases de Dados e Engenharia de Software existe uma variante escolhida para cada um dos 20 níveis.
- Em Programação existem quatro posições de dificuldade, cada uma com duas variantes possíveis.
- O estudante pode escolher «Não sei» sem perder pontos já obtidos noutros itens.
- Não existe desconto adicional por respostas erradas.
- O sistema não apresenta as respostas corretas durante o teste.

### UC-CST-3: Atualizar o Perfil com Novas Evidências

**Objetivo:** garantir que o perfil reflete automaticamente as notas académicas e os testes de nível concluídos.

**Ator primário:** Sistema.

**Cenário de sucesso principal**

1. É registada uma nova nota académica ou é concluído um teste de nível.
2. O sistema identifica as competências a que a evidência diz respeito.
3. O sistema recalcula o nível e o grau de confiança de cada competência afetada.
4. Se existirem apenas notas, o nível é determinado pela evidência académica.
5. Se existir apenas um teste concluído, o nível é determinado pelo resultado do teste.
6. Se existirem notas e teste concluído, o sistema combina as duas fontes segundo o ADR-004.
7. O perfil passa a apresentar o novo nível, a origem da informação e o grau de confiança na consulta seguinte.

**Fluxos alternativos / exceção**

- **A1 — Sem notas nem teste concluído:** o nível permanece indisponível e a confiança é zero.
- **A2 — Teste iniciado mas não concluído:** a sessão não é considerada evidência para o cálculo do nível.
- **E1 — A evidência não está associada a competências:** o registo não deve alterar o nível de nenhuma competência até existir uma associação válida.

**Regras de negócio**

- A atualização do perfil é automática.
- O perfil não deve inventar níveis para competências sem evidência.
- O nível final, a origem e a confiança seguem a estratégia definida no ADR-004.
- As notas académicas continuam a ter maior peso do que o resultado do teste quando existem ambas as fontes.

---

## 5. Resumo funcional dos testes

| Competência | Estrutura | Limite de tempo por item |
|---|---|---|
| Matemática | 20 níveis, duas variantes por nível; escolha múltipla | 90 segundos |
| Bases de Dados | 20 níveis, duas variantes por nível; escolha múltipla | 60 segundos |
| Engenharia de Software | 20 níveis, duas variantes por nível; escolha múltipla | 60 segundos |
| Programação | Quatro problemas de dificuldade crescente, duas variantes por posição | 10 minutos |

Em todos os testes, o estudante só vê o item atual, não pode voltar atrás e pode escolher «Não sei». A resposta errada, a opção «Não sei» e o tempo esgotado não retiram pontos de outros itens. Os detalhes das fórmulas e da segurança encontram-se no ADR-004.

## 6. Alternativas consideradas

| Alternativa | Vantagens | Desvantagens | Decisão |
|---|---|---|---|
| Manter a autoavaliação de 1–5 | Simples e rápida | Subjetiva, suscetível a sobrestimação e pouco comparável | Rejeitada |
| Esperar pelas primeiras notas | Baseia-se em resultados académicos | Deixa estudantes sem histórico sem nível inicial | Rejeitada |
| Usar perguntas sempre da mesma dificuldade | Simples de organizar | Distingue pior estudantes de níveis diferentes | Rejeitada |
| Testes com dificuldade crescente e tempo limitado | Produzem evidência de desempenho em vários níveis | Exigem um banco de perguntas revisto e regras de tempo claras | Escolhida |
| Permitir tentativas ilimitadas | Permite praticar | Reduz a comparabilidade do resultado | Rejeitada |

## 7. Conceitos de domínio relevantes

- **Teste de nível** — avaliação destinada a estimar uma competência.
- **Sessão de teste** — realização de um teste por um estudante numa competência.
- **Pergunta ou problema** — item apresentado durante o teste.
- **Variante** — versão alternativa de um item do mesmo nível ou posição de dificuldade.
- **Resposta** — resposta submetida pelo estudante, incluindo a opção «Não sei» ou uma submissão de código.
- **Resultado do teste** — pontuação final, na escala de 0–20.
- **Grau de confiança** — indicador calculado a partir da quantidade de evidências académicas e da existência de um teste concluído.
- **Origem do nível** — notas académicas, teste de nível ou combinação de ambos.

## 8. Assunções

- O catálogo de competências contém as quatro competências indicadas.
- Cada teste é independente e pode ser realizado separadamente.
- A estratégia de pontuação, os limites de tempo e a confiança são os definidos no ADR-004.
- A autoavaliação de 1–5 deixa de ser usada no novo cálculo de *cold start*. Os dados antigos não são apagados por esta mudança.
- A implementação técnica das sessões, dos bancos de perguntas e do executor de código é definida nas issues de implementação do Sprint 3.
