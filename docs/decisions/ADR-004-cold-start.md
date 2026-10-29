# ADR-004: Estratégia de Cold Start baseada em testes de nível

## Estado

Aprovado

## Contexto

No Sprint 2, o *cold start* foi tratado através de uma autoavaliação em que o estudante atribuía a si próprio um valor entre 1 e 5 por competência. A resposta era convertida para a escala de 0–20 multiplicando por 4 (ou seja, 4–20) e utilizada como evidência inicial; quando já existiam notas, era combinada com elas com um peso decrescente.

Esta solução permitia obter informação inicial, mas tinha limitações importantes:

- **Subjetividade:** a resposta representa a perceção do estudante, não uma demonstração observável da competência.
- **Tendência para sobrestimar:** alguns estudantes podem atribuir a si próprios níveis superiores aos que demonstrariam na prática.
- **Escala pouco calibrada:** dois estudantes podem interpretar de forma diferente o mesmo valor de 1 a 5.
- **Piso artificial:** a conversão de 1–5 para 0–20 por multiplicação por 4 fazia com que a autoavaliação mais baixa valesse 4/20, mesmo quando o estudante não demonstrava qualquer competência.
- **Incerteza elevada:** o sistema não consegue distinguir com segurança entre uma competência demonstrada e uma perceção pessoal.

Esperar pelas primeiras notas também não resolve o problema: um estudante sem histórico continuaria sem informação útil no perfil. É necessário recolher uma evidência inicial mais objetiva, comparável e disponível desde o início.

## Decisão

A autoavaliação de 1–5 deixa de ser a fonte de *cold start* usada no cálculo do perfil. Passa a existir um teste de nível por competência, com dificuldade crescente, tempo limitado e correção baseada nas respostas do estudante.

Os testes são independentes por competência. A sequência de níveis é fixa e **não é adaptativa**: a dificuldade aumenta de forma ordenada, independentemente das respostas anteriores. Em cada nível é selecionada aleatoriamente uma das duas variantes disponíveis.

### 1. Competências abrangidas

O sistema disponibiliza quatro competências:

1. **Programação**
2. **Matemática**
3. **Bases de Dados**
4. **Engenharia de Software**

Cada competência tem o seu próprio teste e resultado. O estudante pode realizar os testes separadamente.

### 2. Formato dos testes

| Competência | Formato | Variantes e ordem | Limite de tempo por item |
|---|---|---|---|
| Matemática | 20 perguntas de escolha múltipla, uma por nível, com quatro opções de resposta e a opção «Não sei» | Duas variantes por nível; o sistema seleciona aleatoriamente uma variante de cada nível. As perguntas são apresentadas do nível 1 ao 20. | 90 segundos por pergunta |
| Bases de Dados | 20 perguntas de escolha múltipla, com tabelas ou consultas pequenas, quatro opções de resposta e a opção «Não sei» | Duas variantes por nível; o sistema seleciona aleatoriamente uma variante de cada nível. As perguntas são apresentadas do nível 1 ao 20. | 60 segundos por pergunta |
| Engenharia de Software | 20 perguntas de escolha múltipla sobre conceitos, código ou cenários, quatro opções de resposta e a opção «Não sei» | Duas variantes por nível; o sistema seleciona aleatoriamente uma variante de cada nível. As perguntas são apresentadas do nível 1 ao 20. | 60 segundos por pergunta |
| Programação | Quatro problemas Java, com dificuldade crescente: fácil, fácil/médio, médio e médio/difícil | Duas variantes por posição de dificuldade; é selecionada uma variante para cada um dos quatro problemas. | 10 minutos por problema |

Nas perguntas de escolha múltipla, existe uma única opção correta entre as quatro opções de resposta. «Não sei» é uma opção explícita e não é considerada uma quinta resposta correta.

Em Programação, o resultado de cada problema depende da proporção de testes de avaliação passados pelo código submetido. Os testes ocultos são mantidos no servidor. Um erro de compilação, a ausência de código ou a opção «Não sei» correspondem a zero pontos nesse problema.

Cada problema de Programação tem de possuir pelo menos um teste de avaliação, para que a proporção de testes passados possa ser calculada sem uma divisão por zero.

Os limites de tempo são limites para responder a cada pergunta ou resolver cada problema. O limite de execução do código é separado do tempo disponível ao estudante: cada execução do código tem um máximo de 5 segundos.

### 3. Cronómetro e navegação

- O tempo começa a contar quando o servidor disponibiliza a pergunta ou o problema atual.
- O cronómetro apresentado no browser é apenas informativo; o servidor é a autoridade para validar o tempo.
- É aceite uma tolerância de 2 segundos para atrasos de comunicação. Depois do limite e da tolerância, a resposta é tratada como não respondida e vale zero.
- Quando o estudante responde, escolhe «Não sei» ou termina o tempo, o sistema avança para o item seguinte.
- Não é possível voltar a perguntas anteriores nem alterar respostas já submetidas.
- Só é apresentada ao browser a pergunta ou o problema atual, não a sequência completa do teste.

### 4. Pontuação sem desconto nos erros

#### Matemática, Bases de Dados e Engenharia de Software

Para cada nível `i`, `acerto_i` vale 1 se a resposta estiver correta e 0 nos restantes casos: resposta errada, «Não sei» ou tempo esgotado.

A pontuação do teste, na escala de 0–20, é:

```text
resultado = 20 × Σ(nível_i × acerto_i) / Σ(nível_i)
```

Como os níveis vão de 1 a 20, o denominador é:

```text
Σ(nível_i) = 1 + 2 + ... + 20 = 210
```

Os níveis mais elevados têm maior influência no resultado, porque representam perguntas de maior dificuldade.

#### Programação

Cada problema tem um peso de acordo com a dificuldade:

| Problema | Dificuldade | Peso |
|---|---|---:|
| 1 | Fácil | 1 |
| 2 | Fácil/médio | 2 |
| 3 | Médio | 3 |
| 4 | Médio/difícil | 4 |
| **Total** | — | **10** |

Para cada problema, `passados_i/total_i` representa a proporção de testes de avaliação passados pelo código. `total_i` tem de ser superior a zero. A pontuação é:

```text
resultado = 20 × Σ(peso_i × passados_i/total_i) / 10
```

O resultado final é apresentado com duas casas decimais. As pontuações ficam na escala de 0–20.

**Não existe desconto por respostas erradas.** Uma resposta errada, «Não sei» ou uma resposta em falta vale zero nesse item, mas não retira pontos obtidos noutros itens.

#### Consequência do palpite aleatório

Nas perguntas com quatro opções de resposta, um estudante que escolha respostas totalmente ao acaso entre as quatro alternativas tem uma probabilidade de 25% de acertar em cada pergunta. O resultado esperado é, por isso, aproximadamente **25% da pontuação máxima**, ou seja, cerca de **5/20**.

Se o sorteio incluir também a opção «Não sei», a probabilidade de acerto desce para 20%, correspondendo a um resultado esperado de aproximadamente **4/20**. Em ambos os casos, «Não sei» vale zero pontos, tal como uma resposta errada.

Esta consequência é aceite como compromisso da estratégia, e não como garantia de que o palpite aleatório é eliminado. A opção «Não sei» permite ao estudante reconhecer que não sabe a resposta sem sofrer uma penalização adicional.

A pontuação é diagnóstica, não uma certificação. Quando existem notas, o teste representa 30% do nível final. Quando só existe teste, a confiança é limitada pelo número de evidências. A progressão de dificuldade melhora a distinção entre níveis, a ponderação valoriza os acertos em níveis mais altos e as variantes reduzem a memorização; nenhuma destas medidas elimina o risco de acerto por acaso.

### 5. Uma única tentativa por competência

Cada estudante pode concluir **uma única tentativa por competência**. Quando o teste termina, o resultado é definitivo e não pode ser repetido ou substituído por uma nova tentativa.

Se a sessão for interrompida antes de terminar, o estudante retoma a mesma sessão, sem reiniciar o teste. As respostas já submetidas não podem ser alteradas e os tempos são mantidos segundo os registos do servidor. Se o tempo de um item expirar durante a interrupção, esse item conta como não respondido.

Esta regra evita que o estudante repita o teste até obter um resultado mais elevado. A possibilidade de retomar uma sessão em curso reduz o risco de uma interrupção acidental criar uma nova tentativa.

### 6. Segurança

- **Cronómetro validado no servidor:** o tempo enviado ou apresentado pelo browser não é considerado prova do tempo decorrido.
- **Respostas corretas protegidas:** a resposta correta não é incluída nos dados enviados ao browser. A correção e o cálculo da pontuação ocorrem no servidor.
- **Perguntas entregues uma de cada vez:** o browser não recebe antecipadamente as perguntas futuras nem a chave de correção.
- **Testes de código isolados:** o código Java é executado num contentor descartável, sem acesso à rede, com limites de CPU, memória, processos, tamanho de código/saída e duração. O contentor usa um utilizador sem privilégios e um sistema de ficheiros só de leitura.
- **Testes ocultos protegidos:** os casos de teste usados para avaliar o código não são enviados ao browser. A resposta da avaliação expõe apenas o resultado necessário, como o número de testes passados e uma eventual mensagem de compilação.
- **Falha do executor:** se o ambiente isolado de execução não estiver disponível, o teste de Programação deve ser indicado como indisponível, sem inventar um resultado e sem impedir a realização dos restantes testes de competência. A falha não consome a tentativa única: o estudante pode realizar o teste de Programação quando o executor estiver novamente disponível.

### 7. Combinação do teste com as notas no perfil

Para cada competência, define-se:

- `M`: média académica da competência, calculada a partir das notas relevantes e dos pesos curriculares já definidos no Sprint 2;
- `R`: resultado do teste de nível concluído, na escala de 0–20;
- `L`: nível final da competência, na escala de 0–20.

O nível final é determinado pela informação disponível:

| Informação disponível | Nível final `L` | Origem |
|---|---|---|
| Existem notas, mas não existe teste concluído | `M` | `ACADEMIC` |
| Existe teste concluído, mas não existem notas relevantes | `R` | `TEST` |
| Existem notas e teste concluído | `0,7 × M + 0,3 × R` | `MIXED` |
| Não existem notas nem teste concluído | `null` | Sem evidência |

A média académica mantém a regra ponderada por competência definida no Sprint 2; esta decisão não altera os pesos curriculares das unidades curriculares. Um teste iniciado mas não concluído não conta como evidência para o nível.

A ausência de informação é representada por `null`, nunca por zero. Zero é uma pontuação possível obtida; `null` significa que não há dados para calcular o nível.

### 8. Grau de confiança

Para cada competência, define-se:

```text
E = número de notas relevantes para a competência
    + 1 se o teste dessa competência estiver concluído

T = E / (E + 2)
```

`T` varia entre 0 e 1. Quando não existe qualquer evidência, `E = 0` e `T = 0`. Um teste concluído sem notas corresponde a uma evidência (`E = 1`) e a uma confiança de aproximadamente `0,33`.

Este valor é um **indicador operacional de confiança baseado na quantidade de evidências**, não uma probabilidade estatística de o nível estar correto. A confiança aumenta com o número de notas relevantes e com a conclusão do teste.

### 9. Transição da estratégia anterior

A autoavaliação de 1–5 deixa de participar no novo cálculo do perfil. Os registos históricos de autoavaliação não devem ser apagados por causa desta alteração; o endpoint antigo deve ser marcado como obsoleto na implementação prevista para S3-02. Os novos resultados de teste passam a ser a fonte de evidência não académica usada no *cold start*.

## Consequências

### Positivas

- O perfil inicial passa a apoiar-se em desempenho demonstrado, em vez de depender apenas da perceção do estudante.
- As quatro competências usam regras explícitas e resultados comparáveis na escala 0–20.
- A dificuldade crescente permite distinguir melhor níveis de conhecimento diferentes.
- O estudante pode obter um nível inicial mesmo sem notas académicas.
- A regra de combinação dá prioridade às notas quando existem ambas as fontes.
- O servidor controla o tempo e a correção, reduzindo a possibilidade de manipulação pelo browser.
- A decisão fica definida antes da implementação das sessões, bancos de perguntas e cálculo do perfil.

### Negativas e compromissos

- Um teste cronometrado pode aumentar a ansiedade ou prejudicar estudantes que precisam de mais tempo.
- Um banco com duas variantes por nível continua limitado; as perguntas têm de ser revistas e protegidas.
- O palpite aleatório continua a poder gerar uma pontuação positiva nas perguntas de escolha múltipla.
- Uma tentativa definitiva aumenta a importância de explicar as regras antes do início e de permitir retomar sessões interrompidas.
- A confiança `T` representa quantidade de evidências, não a dificuldade, a qualidade psicométrica ou a independência dessas evidências.
- A execução de código exige isolamento e infraestrutura adicional; se o executor falhar, o teste de Programação pode ficar temporariamente indisponível.

## Alternativas consideradas

### Manter a autoavaliação de 1–5

Rejeitada como estratégia de *cold start*, porque é subjetiva, permite sobrestimação e não fornece uma escala suficientemente comparável entre estudantes.

### Esperar até existirem notas académicas

Rejeitada porque deixa os estudantes sem histórico sem um nível inicial útil e adia o problema em vez de o resolver.

### Usar perguntas de dificuldade fixa

Rejeitada porque um conjunto apenas fácil pode não distinguir estudantes avançados e um conjunto apenas difícil pode não distinguir níveis iniciais. A progressão de níveis permite recolher evidências em várias dificuldades.

### Usar um teste adaptativo

Rejeitada porque exigiria um banco de itens maior e calibração psicométrica, e tornaria a sequência menos previsível para o estudante. A progressão fixa de níveis já permite recolher evidência em várias dificuldades com um banco mais simples.

### Descontar pontos por respostas erradas

Rejeitada porque introduziria uma penalização adicional e poderia incentivar o estudante a evitar responder. A equipa aceita explicitamente a consequência de que o palpite aleatório pode obter cerca de 5/20 em perguntas com quatro opções de resposta.

### Permitir tentativas ilimitadas

Rejeitada porque os estudantes poderiam repetir o teste até obter uma pontuação mais alta, reduzindo a comparabilidade dos resultados. A retoma da mesma sessão em curso é permitida, mas não equivale a uma nova tentativa.

## Relação com outras decisões

- O comportamento funcional está descrito em [`docs/use-cases/cold-start.md`](../use-cases/cold-start.md).
- Esta ADR tem de ser aprovada antes de S3-02, S3-03 e S3-05 a S3-08 avançarem.
- S3-02 implementa sessões, respostas, validação do tempo e correção dos testes.
- S3-03 integra os resultados no perfil e calcula o nível e a confiança.
- S3-05, S3-06 e S3-07 fornecem os bancos de perguntas de Matemática, Bases de Dados e Engenharia de Software.
- S3-08 implementa o executor isolado e os problemas de Programação.
- `docs/decisoes-sprint2.md` mantém o registo histórico da solução efetivamente implementada no Sprint 2; não descreve a nova decisão como se já estivesse implementada.

### Ponto operacional a definir antes de S3-08

É necessário definir se o estudante pode submeter código várias vezes durante os 10 minutos de cada problema de Programação. Se forem permitidas várias submissões, deve também ficar definido se a pontuação do problema corresponde à melhor submissão ou à última.
