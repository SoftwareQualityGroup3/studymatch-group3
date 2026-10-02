# Caso de Uso de Valor Acrescentado #1: Importar Histórico Académico

Este caso de uso propõe uma funcionalidade adicional para o StudyMatch que permite importar histórico académico através de um ficheiro CSV.

O objetivo é facilitar a introdução de dados académicos já existentes, evitando o registo manual de grandes quantidades de informação e permitindo que o sistema seja utilizado com estudantes que já possuem um percurso académico anterior.

A funcionalidade complementa a área de Trajetória Académica, mas não altera as respetivas regras de domínio nem define detalhes de implementação do processo de importação.

---

## UC-EX1-1: Importar Histórico Académico

### Nome e Objetivo

**Nome:** Importar Histórico Académico

**Objetivo:** Permitir que os Serviços Académicos importem, em lote, informação académica existente sobre estudantes, reduzindo o registo manual e facilitando a integração de dados anteriores no StudyMatch.

### Ator(es) Primário(s)

- **Serviços Académicos** — selecionam e submetem os dados académicos a importar e acompanham o resultado da operação.

De acordo com os atores e permissões definidos para o StudyMatch, os Serviços Académicos são responsáveis pela gestão administrativa dos estudantes e possuem permissões de escrita sobre a informação académica necessária ao funcionamento do sistema.

### Cenário de Sucesso Principal

1. Os Serviços Académicos autenticam-se na plataforma StudyMatch.
2. O ator inicia uma nova importação de histórico académico.
3. O ator seleciona um ficheiro CSV contendo os dados académicos a importar.
4. O sistema verifica se a informação necessária para identificar os estudantes e os respetivos registos académicos está presente.
5. O sistema valida os registos antes de os adicionar ao histórico académico.
6. O sistema apresenta um resumo da importação, indicando o número de registos válidos e eventuais problemas encontrados.
7. Os Serviços Académicos confirmam a importação.
8. O sistema associa os registos válidos aos respetivos estudantes, preservando o histórico académico já existente.
9. O sistema apresenta o resultado final da operação.

### Fluxos Alternativos / Exceção

**A1 — Importação com alguns registos inválidos**

Se apenas alguns registos apresentarem problemas, o sistema identifica esses registos e permite que os restantes dados válidos sejam importados.

Os registos inválidos não são adicionados ao histórico enquanto os problemas não forem corrigidos.

**A2 — Estudante já possui histórico académico**

Se o estudante já possuir histórico no StudyMatch, os novos registos são adicionados ao histórico existente sem eliminar tentativas ou resultados anteriores.

**E1 — Estudante não identificado**

Se um registo estiver associado a um identificador institucional que não corresponda a nenhum estudante registado no StudyMatch, o sistema sinaliza o registo e não o importa automaticamente.

**E2 — Registo duplicado**

Se a informação a importar corresponder a um registo académico já existente, o sistema identifica o possível duplicado e impede a criação automática de uma segunda cópia.

**E3 — Informação obrigatória em falta**

Se um registo não possuir informação suficiente para representar corretamente a tentativa académica, o sistema considera-o inválido e apresenta o motivo.

**E4 — Ator sem permissão**

Se um ator sem permissões administrativas tentar realizar uma importação, o sistema nega a operação.

### Regras de Negócio e Restrições

- Apenas os **Serviços Académicos** podem importar histórico académico.
- Cada registo importado deve estar associado a um estudante existente no StudyMatch.
- A importação nunca deve eliminar informação académica anteriormente registada.
- Registos duplicados não devem originar novas tentativas no histórico.
- Registos inválidos devem ser identificados antes da confirmação da importação.
- A informação importada deve respeitar as mesmas regras de histórico académico definidas para a Trajetória Académica.
- A importação é uma forma alternativa de introduzir informação; não cria um modelo de histórico académico diferente.
- O sistema deve permitir distinguir registos aceites de registos rejeitados durante uma importação.

### Conceitos de Domínio Revelados

- **Importação de Histórico Académico** — operação de entrada em lote de dados académicos existentes.
- **Ficheiro de Importação** — conjunto de registos submetidos numa única operação.
- **Registo de Importação** — elemento individual da informação académica submetida.
- **Resultado de Importação** — resumo dos registos aceites, rejeitados ou sinalizados.
- **Registo Inválido** — registo que não cumpre a informação ou regras necessárias para integrar o histórico.
- **Registo Duplicado** — informação que corresponde a um registo académico já existente.
- **Estudante** — estudante ao qual os dados importados ficam associados.
- **Trajetória Académica** — histórico académico no qual os registos válidos são integrados.
- **Tentativa** — ocorrência individual de uma inscrição ou avaliação que pode resultar de um registo importado.

---

## Valor Acrescentado para o StudyMatch

A introdução manual do histórico académico de vários estudantes pode exigir um número elevado de operações e aumentar a possibilidade de erros.

A importação em lote acrescenta valor ao StudyMatch porque:

- reduz o trabalho manual dos Serviços Académicos;
- facilita a introdução de dados de estudantes que já possuem histórico académico;
- permite uma adoção mais rápida do StudyMatch em instituições que já possuem informação académica armazenada;
- reduz a repetição de operações de registo;
- ajuda a disponibilizar mais rapidamente informação relevante para trajetórias, perfis e futuras funcionalidades de formação de grupos.

Esta funcionalidade não substitui a Trajetória Académica. A trajetória continua responsável pela representação e organização do histórico; a importação constitui apenas uma forma adicional de fornecer dados a essa área.

---

## Alinhamento com Atores e Outros Casos de Uso

### Identidade do Estudante

A importação pressupõe que o estudante já existe no StudyMatch e pode ser identificado através do seu identificador institucional.

A criação e representação da identidade do estudante continuam a ser responsabilidade dos casos de uso de Identidade do Estudante.

### Trajetória Académica

Os registos académicos válidos são incorporados na trajetória do estudante.

As regras relativas à preservação de tentativas anteriores, reprovações, repetições e anos letivos continuam a ser definidas pela área de Trajetória Académica.

### Atores e Permissões

De acordo com a definição atual dos atores:

- **Serviços Académicos** — podem executar a importação;
- **Docente** — não realiza importações de histórico académico;
- **Estudante** — não realiza importações e mantém acesso de leitura aos seus próprios dados.

---

## Questões em Aberto e Assunções

- Assume-se que os estudantes referidos no ficheiro já estão registados no StudyMatch.
- Fica em aberto se, no futuro, uma importação poderá também criar automaticamente estudantes ainda inexistentes.
- Fica em aberto quais os campos obrigatórios do ficheiro de importação; esta definição deverá acompanhar a evolução do modelo da Trajetória Académica.
- Fica em aberto se serão suportados formatos adicionais para além de CSV.
- Fica em aberto se uma importação parcialmente válida deve exigir confirmação explícita antes de guardar os registos válidos.
- Assume-se que erros num registo individual não obrigam necessariamente a rejeitar toda a importação.
- O formato técnico do ficheiro, a biblioteca utilizada para o processar e os detalhes da interface de importação ficam fora do âmbito deste caso de uso.