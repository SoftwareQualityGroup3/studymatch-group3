# Modelo de Domínio Inicial – Parte 1: Conceitos e Atributos

## Objetivo

Este documento define os conceitos do domínio do StudyMatch que são relevantes para a gestão da identidade académica, da trajetória do estudante, do perfil, do cold start e do contexto de agrupamento.

Não inclui entidades de implementação (controllers, repositórios, tabelas, serviços, endpoints), nem qualquer algoritmo de matching ou formação de grupos. A base de análise são os casos de uso definidos nas áreas de identidade, trajetória académica, perfil, competências, cold start e contexto de agrupamento.

---

## 1. Conceitos de Domínio

### 1.1 Estudante
Descrição:
Representa a pessoa cujo percurso académico é acompanhado pelo sistema.

Atributos importantes:
- identificador institucional
- nome
- contacto institucional
- estado do estudante
- data de registo

Casos de uso que o revelam:
- UC-IDE-1
- UC-IDE-2
- UC-IDE-3

---

### 1.2 Curso
Descrição:
Programa académico em que o estudante está inscrito.

Atributos importantes:
- código
- nome
- tipo de curso
- plano de estudos associado

Casos de uso que o revelam:
- UC-IDE-1
- UC-IDE-2
- UC-IDE-3

---

### 1.3 Plano de Estudos
Descrição:
Conjunto de unidades curriculares que compõem o percurso académico de um estudante num determinado curso.

Atributos importantes:
- designação
- versão
- curso a que pertence
- unidades curriculares associadas

Casos de uso que o revelam:
- UC-IDE-1
- UC-IDE-3
- UC-TRA-1

---

### 1.4 Contexto de Curso
Descrição:
Situação académica do estudante num dado momento, incluindo curso, plano de estudos, ano curricular e período de validade.

Atributos importantes:
- estudante
- curso
- plano de estudos
- ano curricular
- ano letivo de ingresso
- data de início de vigência
- data de fim de vigência

Casos de uso que o revelam:
- UC-IDE-1
- UC-IDE-2
- UC-IDE-3

---

### 1.5 Ano Curricular
Descrição:
Ano do percurso académico em que o estudante se encontra.

Atributos importantes:
- número do ano
- referência ao plano de estudos
- data de início
- data de fim

Casos de uso que o revelam:
- UC-IDE-1
- UC-IDE-2
- UC-IDE-3

---

### 1.6 Identificador Institucional
Descrição:
Identificação única do estudante dentro da instituição.

Atributos importantes:
- valor único
- tipo de identificador
- data de atribuição

Casos de uso que o revelam:
- UC-IDE-1
- UC-IDE-2

---

### 1.7 Trajetória Académica
Descrição:
Conjunto histórico das inscrições, tentativas e resultados do estudante ao longo do seu percurso académico.

Atributos importantes:
- estudante
- lista de inscrições
- lista de tentativas
- ano letivo
- progressão no curso

Casos de uso que o revelam:
- UC-TRA-1
- UC-TRA-2
- UC-PER-1

---

### 1.8 Unidade Curricular
Descrição:
Unidade de ensino do plano de estudos, associada a um curso e a uma trajetória académica.

Atributos importantes:
- código
- nome
- créditos/ECTS
- ano letivo de oferta
- estado de disponibilidade

Casos de uso que o revelam:
- UC-TRA-1
- UC-TRA-2
- UC-COM-2
- UC-GRP-1

---

### 1.9 Inscrição
Descrição:
Relação entre um estudante e uma unidade curricular num determinado ano letivo.

Atributos importantes:
- estudante
- unidade curricular
- ano letivo
- estado da inscrição

Casos de uso que o revelam:
- UC-TRA-1
- UC-TRA-2

---

### 1.10 Tentativa
Descrição:
Registo individual de uma avaliação ou tentativa de uma unidade curricular.

Atributos importantes:
- unidade curricular
- estudante
- número da tentativa
- época de avaliação
- classificação
- estado de conclusão
- data da tentativa

Casos de uso que o revelam:
- UC-TRA-1
- UC-TRA-2

---

### 1.11 Classificação
Descrição:
Resultado obtido numa tentativa de unidade curricular.

Atributos importantes:
- valor
- escala de avaliação
- data de atribuição
- tentativa a que se refere

Casos de uso que o revelam:
- UC-TRA-1
- UC-TRA-2

---

### 1.12 Estado de Conclusão
Descrição:
Estado atual de uma unidade curricular ou de uma tentativa, como aprovado, reprovado ou em curso.

Atributos importantes:
- valor do estado
- data de atualização
- referência à unidade curricular

Casos de uso que o revelam:
- UC-TRA-1
- UC-TRA-2

---

### 1.13 Perfil do Estudante
Descrição:
Visão consolidada da informação académica e das competências do estudante, derivada da sua trajetória e de evidências relevantes.

Atributos importantes:
- estudante
- estado do perfil
- grau de completude
- competências associadas
- evidências relevantes
- data da última atualização

Casos de uso que o revelam:
- UC-PER-1
- UC-PER-2
- UC-CST-1
- UC-CST-3

---

### 1.14 Competência
Descrição:
Capacidade, habilidade ou conjunto de conhecimentos valorizados no contexto académico e de agrupamento.

Atributos importantes:
- nome
- descrição
- categoria
- nível de competência (quando aplicável)
- evidências associadas

Casos de uso que o revelam:
- UC-COM-1
- UC-PER-1
- UC-PER-2
- UC-CST-1
- UC-CST-2

---

### 1.15 Categoria de Competência
Descrição:
Classificação de uma competência por tipo, por exemplo técnica ou transversal.

Atributos importantes:
- nome da categoria
- descrição

Casos de uso que o revelam:
- UC-COM-1
- UC-COM-2

---

### 1.16 Evidência
Descrição:
Elementos que sustentam a afirmação de que um estudante desenvolveu ou possui uma competência.

Atributos importantes:
- tipo de evidência
- descrição
- origem
- data
- grau de confiança
- estado de validação

Casos de uso que o revelam:
- UC-COM-3
- UC-PER-1
- UC-PER-2
- UC-CST-3

---

### 1.17 Evidência Curricular
Descrição:
Evidência derivada de uma unidade curricular ou do percurso académico formal.

Atributos importantes:
- unidade curricular associada
- classificação
- peso de contribuição
- data da avaliação

Casos de uso que o revelam:
- UC-COM-2
- UC-PER-1
- UC-PER-2

---

### 1.18 Evidência Não Curricular
Descrição:
Evidência associada a atividades, projetos, certificados, atividades extra curriculares ou autoavaliação.

Atributos importantes:
- tipo de evidência
- origem
- data
- estado de validação
- grau de confiança

Casos de uso que o revelam:
- UC-COM-3
- UC-PER-1
- UC-PER-2
- UC-CST-2

---

### 1.19 Autoavaliação
Descrição:
Avaliação submetida pelo próprio estudante relativamente ao seu nível em determinadas competências.

Atributos importantes:
- estudante
- competência avaliada
- nível indicado
- data da resposta
- origem da evidência

Casos de uso que o revelam:
- UC-CST-1
- UC-CST-2
- UC-CST-3

---

### 1.20 Estado do Perfil
Descrição:
Indicador do grau de completude do perfil do estudante, especialmente relevante em situações de cold start.

Atributos importantes:
- valor do estado (em construção, parcial, completo)
- data da última atualização
- estudante associado

Casos de uso que o revelam:
- UC-CST-1
- UC-CST-3
- UC-PER-1

---

### 1.21 Contexto de Agrupamento
Descrição:
Conjunto de parâmetros que definem o ambiente em que os grupos serão formados no futuro.

Atributos importantes:
- unidade curricular
- atividade associada
- tamanho alvo
- restrições
- estado do contexto
- data de criação

Casos de uso que o revelam:
- UC-GRP-1
- UC-GRP-2

---

### 1.22 Atividade
Descrição:
Tarefa académica específica dentro de uma unidade curricular, como projeto, trabalho ou laboratório.

Atributos importantes:
- nome
- descrição
- unidade curricular à qual pertence
- prazo

Casos de uso que o revelam:
- UC-GRP-1
- UC-GRP-2

---

### 1.23 Tamanho Alvo
Descrição:
Número de estudantes pretendido por grupo, ou intervalo mínimo/máximo para a formação dos grupos.

Atributos importantes:
- valor fixo
- mínimo
- máximo
- unidade

Casos de uso que o revelam:
- UC-GRP-1
- UC-GRP-2

---

### 1.24 Restrição de Agrupamento
Descrição:
Condição adicional que o contexto de agrupamento impõe ao processo futuro de formação de grupos.

Atributos importantes:
- tipo de restrição
- descrição
- nível de obrigatoriedade

Casos de uso que o revelam:
- UC-GRP-1
- UC-GRP-2

---

## 2. Conceitos que suportam Cold Start

Os seguintes conceitos são especialmente relevantes no caso de estudantes sem histórico académico ou com histórico pouco relevante:

- Estado do Perfil
- Autoavaliação
- Evidência Não Curricular
- Grau de Confiança
- Perfil do Estudante

Estes conceitos permitem representar o estado inicial do perfil sem fixar qualquer algoritmo de cálculo. A ideia é que o perfil evolui progressivamente à medida que surgem novas evidências.

---

## 3. Conceitos que suportam histórico académico e perfis

Os conceitos principais para esse suporte são:

- Estudante
- Trajetória Académica
- Unidade Curricular
- Inscrição
- Tentativa
- Classificação
- Perfil do Estudante
- Competência
- Evidência

A combinação destes elementos permite construir uma visão do estudante tanto na perspetiva do percurso académico quanto na perspetiva das competências e evidências.

---

## 4. Decisões e questões em aberto

### Decisões adotadas
- O modelo de domínio usa termos do glossário da issue #15.
- Não se incluem conceitos de implementação.
- O cold start é tratado como um estado de perfil inicial com evidências limitadas.
- O contexto de agrupamento é representado como um conceito do domínio, mas não se define qualquer algoritmo de agrupamento.

### Questões em aberto
- Como deve ser representado o nível de competência quando não existem evidências suficientes?
- Como deve ser tratado o peso da autoavaliação face às evidências curriculares?
- Qual é a forma exata de representar o grau de confiança das evidências?
- Que restrições de agrupamento terão de ser modeladas no futuro?

---

## 5. Resumo da tua parte

A tua parte deve responder a estas perguntas:

- Que conceitos existem no domínio?
- Quais são os atributos mais importantes de cada conceito?
- Quais casos de uso revelam cada conceito?
- Que conceitos são relevantes para o histórico académico, para o perfil e para o cold start?
- Que conceitos não pertencem ao modelo de domínio e devem ser excluídos?

Com este documento, o teu colega pode então continuar com:
- as relações entre conceitos;
- as cardinalidades;
- as restrições do domínio;
- e a construção do diagrama UML final.