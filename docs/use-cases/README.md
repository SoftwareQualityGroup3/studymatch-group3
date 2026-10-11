# Índice de Casos de Uso - StudyMatch

Este documento consolida o modelo de casos de uso do StudyMatch, servindo como índice coerente para as diferentes áreas funcionais definidas no âmbito do projeto.

## Tabela Geral de Casos de Uso

| Área / Tema | ID | Nome do Caso de Uso | Descrição / Objetivo | Ficheiro de Referência |
| :--- | :--- | :--- | :--- | :--- |
| **Identidade do Estudante** | **UC-IDE-1** | Registar Estudante e Contexto de Curso | Registar a identidade do estudante e o respetivo contexto académico inicial. | [identidade-estudante.md](./identidade-estudante.md) |
| | **UC-IDE-2** | Consultar Identidade e Contexto de Curso | Consultar dados de identidade e contexto, respeitando permissões por papel. | [identidade-estudante.md](./identidade-estudante.md) |
| | **UC-IDE-3** | Atualizar Contexto de Curso | Atualizar o percurso académico (ano, plano, curso) preservando o histórico. | [identidade-estudante.md](./identidade-estudante.md) |
| **Trajetória Académica** | **UC-TRA-1** | Consultar Trajetória Académica | Visualizar o histórico cronológico de inscrições, tentativas e resultados. | [trajetoria-academica.md](./trajetoria-academica.md) |
| | **UC-TRA-2** | Registar Resultado de Unidade Curricular | Registar uma nova tentativa ou resultado numa unidade curricular. | [trajetoria-academica.md](./trajetoria-academica.md) |
| **Perfil do Estudante** | **UC-PER-1** | Consultar Perfil do Estudante | Visualizar a visão consolidada da informação académica e competências. | [perfil-estudante.md](./perfil-estudante.md) |
| | **UC-PER-2** | Consultar Evidências e Competências do Perfil | Consultar as competências e as respetivas evidências curriculares/não curriculares. | [perfil-estudante.md](./perfil-estudante.md) |
| **Sistema de Competências** | **UC-COM-1** | Definir Competência | Criar e descrever uma nova competência no catálogo administrativo. | [competencias.md](./competencias.md) |
| | **UC-COM-2** | Associar Competência a Unidade Curricular | Associar competências a unidades curriculares com respetivos pesos. | [competencias.md](./competencias.md) |
| | **UC-COM-3** | Associar Competência a Evidência Não Curricular| Permitir que projetos ou atividades alimentem competências. | [competencias.md](./competencias.md) |
| **Cold Start** | **UC-CST-1** | Consultar Estado do Perfil e Próximos Passos | Mostrar o grau de completude do perfil e sugestões de melhoria inicial. | [cold-start.md](./cold-start.md) |
| | **UC-CST-2** | Registar Autoavaliação Inicial | Permitir ao estudante fornecer evidência inicial por questionário. | [cold-start.md](./cold-start.md) |
| | **UC-CST-3** | Atualizar o Perfil com Novas Evidências | Evoluir o perfil de forma automática à medida que surgem novas evidências. | [cold-start.md](./cold-start.md) |
| **Contexto de Agrupamento** | **UC-GRP-1** | Definir Contexto de Agrupamento | Configurar parâmetros, tamanho alvo e restrições para futuros grupos. | [contexto-agrupamento.md](./contexto-agrupamento.md) |
| | **UC-GRP-2** | Consultar Contexto de Agrupamento | Consultar os contextos de agrupamento ativos ou passados. | [contexto-agrupamento.md](./contexto-agrupamento.md) |
| **Valor Acrescentado** | **UC-EX1-1** | Importar Histórico Académico | Importação em lote de dados académicos pré-existentes via CSV. | [extra-1.md](./extra-1.md) |
| | **UC-EX2-1** | Pedir Correção de Dados do Perfil | Permitir ao estudante submeter pedidos de retificação de dados académicos. | [extra-2.md](./extra-2.md) |
