# Caso de Uso: Identidade do Estudante

## Atores e Permissões

Lista de atores proposta para o StudyMatch, a confirmar pela equipa na issue #7 e a reutilizar nas restantes áreas de casos de uso.

| Ator | Descrição | Operações principais | Permissões |
| --- | --- | --- | --- |
| **Estudante** | Utilizador cujo percurso académico é representado no sistema. | Consultar a sua identidade e o contexto do curso; confirmar os seus dados; consultar o seu perfil e trajetória. | Leitura dos seus próprios dados. Sem acesso aos dados de outros estudantes. |
| **Docente** | Responsável por unidades curriculares e atividades em que os grupos serão formados. | Consultar o contexto de curso dos estudantes inscritos nas suas unidades curriculares; registar resultados. | Leitura restrita aos estudantes das suas unidades curriculares. Sem acesso a dados fora desse âmbito. |
| **Serviços Académicos** | Responsáveis pela gestão administrativa de estudantes, cursos e planos de estudos. | Registar estudantes; atualizar o contexto de curso; corrigir dados de identidade. | Leitura e escrita sobre a identidade e o contexto de curso de qualquer estudante. |

> Os documentos de outras áreas (por exemplo, `trajetoria-academica.md`) usam "Docente / Serviços Académicos" como ator agrupado. Esta lista separa-os porque as permissões são diferentes. A equipa deve confirmar se esta separação se mantém.

---

## UC-IDE-1: Registar Estudante e Contexto de Curso

### Nome e Objetivo

**Nome:** Registar Estudante e Contexto de Curso

**Objetivo:** Permitir que um estudante seja representado no sistema com a sua identidade e o seu contexto de curso (curso, plano de estudos, ano letivo de ingresso e ano curricular), servindo de base ao percurso académico e ao perfil.

### Ator(es) Primário(s)

- **Serviços Académicos** — responsáveis pelo registo do estudante e do respetivo contexto de curso.
- **Estudante** *(ator secundário)* — confirma os seus dados após o registo.

### Cenário de Sucesso Principal

1. Os Serviços Académicos iniciam o registo de um novo estudante.
2. Indicam a identificação do estudante: identificador institucional, nome e contacto institucional.
3. Indicam o contexto de curso: curso, plano de estudos, ano letivo de ingresso e ano curricular atual.
4. O sistema valida que a informação obrigatória está completa e que o identificador institucional ainda não existe.
5. O sistema regista o estudante com o estado **ativo** e associa-lhe o respetivo contexto de curso.
6. O sistema confirma a conclusão do registo e indica que o estudante ainda não possui histórico académico.

### Fluxos Alternativos / Exceções

**A1 — Estudante sem histórico académico**

Após o passo 5, se não existir informação relativa a unidades curriculares anteriores, o estudante fica registado apenas com a identidade e o contexto de curso. O sistema não bloqueia nenhuma funcionalidade por essa ausência, ficando o tratamento de *cold start* fora do âmbito deste caso de uso.

**A2 — Estudante com histórico anterior**

Se o estudante ingressar com unidades curriculares já realizadas, o registo da identidade é concluído e o histórico é associado posteriormente através da área de trajetória académica.

**E1 — Estudante já registado**

No passo 4, se o identificador institucional já existir, o sistema rejeita o novo registo e indica que já existe um estudante associado a esse identificador.

**E2 — Informação obrigatória em falta ou inválida**

No passo 4, se faltar informação obrigatória ou se o plano de estudos indicado não pertencer ao curso selecionado, o sistema rejeita o registo e indica os dados que devem ser corrigidos.

### Regras de Negócio e Restrições

- Cada estudante é identificado de forma única pelo seu identificador institucional.
- Um estudante tem de estar associado a, pelo menos, um curso e a um plano de estudos.
- Apenas os Serviços Académicos podem registar estudantes.
- Devem ser recolhidos apenas os dados necessários ao funcionamento do sistema, respeitando o princípio da minimização de dados pessoais.
- A ausência de histórico académico não impede o registo.

### Conceitos de Domínio Revelados

- **Estudante** — pessoa cujo percurso académico é representado no sistema.
- **Identificador Institucional** — identificação única do estudante na instituição.
- **Curso** — programa de estudos em que o estudante está inscrito.
- **Plano de Estudos** — conjunto de unidades curriculares que compõem o curso numa determinada versão.
- **Ano Letivo de Ingresso** — ano letivo em que o estudante iniciou o curso.
- **Ano Curricular** — ano do plano de estudos em que o estudante se encontra.
- **Estado do Estudante** — situação atual do estudante (por exemplo, ativo, suspenso ou concluído).

---

## UC-IDE-2: Consultar Identidade e Contexto de Curso

### Nome e Objetivo

**Nome:** Consultar Identidade e Contexto de Curso

**Objetivo:** Permitir que um ator autorizado consulte a identidade e o contexto de curso de um estudante, respeitando os limites de acesso definidos para cada papel.

### Ator(es) Primário(s)

- **Estudante** — consulta os seus próprios dados.
- **Docente** — consulta os dados dos estudantes das suas unidades curriculares.
- **Serviços Académicos** — consultam os dados de qualquer estudante.

### Cenário de Sucesso Principal

1. O ator autentica-se na plataforma StudyMatch.
2. O ator solicita a consulta da identidade e do contexto de curso de um estudante; no caso do estudante, a consulta refere-se aos seus próprios dados.
3. O sistema verifica se o ator tem permissão para aceder aos dados desse estudante.
4. O sistema apresenta a informação permitida para o papel do ator, incluindo, quando aplicável, o nome, curso, plano de estudos, ano curricular, ano letivo de ingresso e estado.
5. O ator visualiza a informação apresentada.

### Fluxos Alternativos / Exceções

**A1 — Docente consulta estudante da sua unidade curricular**

No passo 4, o sistema apresenta ao docente apenas a informação necessária ao contexto académico (por exemplo, nome, curso e ano curricular), omitindo dados pessoais que não sejam relevantes para o seu papel.

**E1 — Ator sem permissão**

No passo 3, se o ator não tiver permissão para aceder aos dados do estudante, o sistema nega o acesso e regista a tentativa.

**E2 — Estudante inexistente**

No passo 3, se o estudante não existir, o sistema informa que o estudante não foi encontrado, sem revelar informação adicional.

### Regras de Negócio e Restrições

- O estudante apenas pode consultar os seus próprios dados, em modo de leitura.
- O docente apenas pode aceder aos dados dos estudantes inscritos nas suas unidades curriculares.
- A informação apresentada a cada ator deve limitar-se ao que é necessário para o respetivo papel.
- Os acessos negados devem ficar registados.

### Conceitos de Domínio Revelados

- **Estudante**, **Curso**, **Plano de Estudos**, **Ano Curricular** e **Estado do Estudante** (ver UC-IDE-1).
- **Ator / Papel** — perfil de utilização que determina as operações e os dados acessíveis.
- **Permissão de Acesso** — regra que determina a relação entre um ator, os estudantes e os dados que pode consultar.

---

## UC-IDE-3: Atualizar Contexto de Curso

### Nome e Objetivo

**Nome:** Atualizar Contexto de Curso

**Objetivo:** Permitir que o contexto de curso de um estudante seja atualizado ao longo do tempo (por exemplo, mudança de ano curricular, plano de estudos, curso ou estado), sem perder o contexto anterior.

### Ator(es) Primário(s)

- **Serviços Académicos** — responsáveis pela atualização do contexto de curso do estudante.

### Cenário de Sucesso Principal

1. Os Serviços Académicos selecionam o estudante.
2. Indicam a alteração (por exemplo, avanço de ano curricular, mudança de plano de estudos ou alteração de estado) e a data a partir da qual produz efeitos.
3. O sistema valida a alteração de acordo com as regras do curso e do plano de estudos.
4. O sistema regista o novo contexto e mantém o contexto anterior no histórico.
5. O sistema confirma a conclusão da atualização.

### Fluxos Alternativos / Exceções

**A1 — Mudança de curso**

Se o estudante mudar de curso, o sistema cria um novo contexto de curso. O contexto e o histórico do curso anterior mantêm-se associados ao estudante.

**E1 — Alteração inválida**

No passo 3, se a alteração violar alguma regra (por exemplo, se o ano curricular não existir no plano de estudos), o sistema rejeita a alteração e indica o motivo.

**E2 — Data incoerente**

No passo 2, se a data de início de vigência se sobrepuser a outro contexto do mesmo estudante, o sistema rejeita a alteração.

### Regras de Negócio e Restrições

- Uma atualização nunca elimina o contexto de curso anterior.
- Um estudante tem, em cada momento, no máximo um contexto de curso ativo por curso.
- Apenas os Serviços Académicos podem atualizar o contexto de curso.
- A informação necessária para o perfil e para a formação de grupos deve poder ser reconstruída para qualquer momento do percurso académico.

### Conceitos de Domínio Revelados

- **Contexto de Curso** — associação entre estudante, curso, plano de estudos e ano curricular, válida durante determinado período.
- **Período de Validade** — intervalo de tempo durante o qual um contexto de curso é válido.
- **Histórico de Contexto** — sequência dos contextos de curso de um estudante ao longo do tempo.

---

## Informação Necessária para Representar o Estudante e o seu Curso

### Identidade do Estudante

- Identificador institucional (único).
- Nome.
- Contacto institucional.
- Estado do estudante (ativo, suspenso ou concluído).

### Contexto de Curso e de Ano

- Curso e plano de estudos.
- Ano letivo de ingresso.
- Ano curricular atual.
- Período de validade do contexto, para permitir a manutenção do histórico.

### Fora do Âmbito desta Área

- Unidades curriculares, inscrições, tentativas e classificações (ver `trajetoria-academica.md`).
- Competências e perfil (áreas próprias).

---

## Questões em Aberto e Assunções

- Assume-se que o registo de estudantes é efetuado pelos Serviços Académicos. Fica em aberto se o estudante poderá efetuar o seu próprio registo, sujeito a validação.
- Assume-se que existe um mecanismo de autenticação, mas a sua implementação (credenciais próprias, autenticação institucional, etc.) fica fora do âmbito do Sprint 1.
- Fica em aberto se é necessário representar o grau (por exemplo, licenciatura ou mestrado) como parte do curso ou do plano de estudos.
- Fica em aberto como tratar estudantes inscritos em mais do que um curso em simultâneo (por exemplo, dupla titulação). Nesta fase, assume-se um contexto ativo por estudante e por curso.
- Fica em aberto que dados pessoais adicionais (por exemplo, fotografia ou data de nascimento) serão necessários. Assume-se, nesta fase, a recolha do mínimo necessário, por razões de privacidade.
- Assume-se que a lista de atores e permissões acima será confirmada na issue #7. Caso a equipa defina atores ou permissões diferentes, os casos de uso deverão ser ajustados em conformidade.