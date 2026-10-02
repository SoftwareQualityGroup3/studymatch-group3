# Caso de Uso: Perfil do Estudante

O perfil do estudante apresenta, de forma consolidada, a informação académica e de competências relevante sobre um estudante. O perfil combina a trajetória académica, incluindo unidades curriculares, classificações e progressão no curso, com as competências desenvolvidas através de unidades curriculares e de outras evidências.

O perfil não substitui o histórico académico: a trajetória continua a ser construída a partir das inscrições e tentativas registadas ao longo dos anos letivos. Da mesma forma, as competências apresentadas no perfil resultam das evidências associadas a cada competência.

---

## UC-PER-1: Consultar Perfil do Estudante

### Nome e Objetivo

**Nome:** Consultar Perfil do Estudante

**Objetivo:** Permitir que um estudante ou outro ator autorizado consulte uma visão consolidada da sua informação académica e das competências desenvolvidas ao longo do percurso académico.

### Ator(es) Primário(s)

- **Estudante** — consulta o seu próprio perfil.
- **Docente / Serviços Académicos** — pode consultar o perfil de estudantes quando possuir autorização para o efeito.

As permissões concretas dos diferentes atores dependem da definição final de permissões da plataforma.

### Cenário de Sucesso Principal

1. O estudante autentica-se na plataforma StudyMatch.
2. O estudante acede ao seu perfil.
3. O sistema apresenta a informação académica relevante, incluindo a progressão no curso e informação sobre as unidades curriculares concluídas.
4. O sistema apresenta as competências associadas ao estudante.
5. Para cada competência, são apresentadas as evidências que contribuem para o respetivo nível, quando aplicável.
6. O estudante consegue consultar a informação de forma organizada, distinguindo a trajetória académica das competências desenvolvidas.

### Fluxos Alternativos / Exceção

**A1 — Perfil com pouca informação**

Se o estudante ainda tiver poucas unidades curriculares concluídas ou poucas evidências associadas a competências, o perfil apresenta a informação disponível sem assumir competências que não tenham evidência suficiente.

**A2 — Competência sem evidência**

Se uma competência não tiver nenhuma associação a unidades curriculares ou evidência relevante para o estudante, esta não é apresentada como competência desenvolvida no seu perfil.

**A3 — Evidência não curricular pendente de validação**

Se uma evidência não curricular estiver sujeita a validação, esta pode ser apresentada como evidência pendente, mas não contribui para o perfil enquanto a validação exigida não tiver ocorrido.

**E1 — Ator sem permissão**

Se um ator tentar consultar o perfil de um estudante sem autorização, o acesso é negado.

### Regras de Negócio e Restrições

- O estudante tem acesso de leitura ao seu próprio perfil.
- Outros atores apenas podem consultar perfis quando possuírem autorização.
- A informação académica apresentada deve respeitar o histórico da trajetória académica, mantendo as tentativas anteriores.
- Uma competência só deve ser considerada relevante para o perfil quando existir pelo menos uma fonte de evidência associada.
- As competências podem ser alimentadas por unidades curriculares e por evidências não curriculares.
- O nível de uma competência é baseado nas evidências disponíveis, não existindo nesta fase uma fórmula definitiva para o seu cálculo.
- Evidências não curriculares têm, por defeito, menor peso do que evidências curriculares, salvo validação explícita.

### Conceitos de Domínio Revelados

- **Perfil do Estudante**
- **Trajetória Académica**
- **Progressão no Curso**
- **Unidade Curricular**
- **Competência**
- **Evidência**
- **Evidência Curricular**
- **Evidência Não Curricular**
- **Grau de Confiança**
- **Validação de Evidência**

---

## UC-PER-2: Consultar Evidências e Competências do Perfil

### Nome e Objetivo

**Nome:** Consultar Evidências e Competências do Perfil

**Objetivo:** Permitir consultar as competências associadas ao estudante e compreender quais as evidências académicas ou não curriculares que contribuem para cada uma.

### Ator(es) Primário(s)

- **Estudante** — consulta as suas competências e respetivas evidências.
- **Docente / Coordenador de curso** — pode consultar estas informações quando possuir autorização.

### Cenário de Sucesso Principal

1. O ator acede à área de competências do perfil do estudante.
2. O sistema apresenta as competências relevantes para o estudante.
3. Para cada competência, apresenta o respetivo nível, quando este puder ser determinado.
4. O sistema apresenta as evidências que contribuíram para a competência.
5. As evidências provenientes de unidades curriculares são relacionadas com as respetivas unidades curriculares.
6. As evidências não curriculares são identificadas separadamente e, quando aplicável, apresentam o seu estado de validação.

### Fluxos Alternativos / Exceção

**A1 — Várias fontes para a mesma competência**

Se uma competência for alimentada por várias unidades curriculares ou evidências, o perfil apresenta as diferentes fontes que contribuíram para essa competência.

**A2 — Evidência não validada**

Se uma evidência não curricular ainda não tiver sido validada, o seu estado é identificado e a evidência apenas contribui para o perfil de acordo com as regras de validação definidas.

**E1 — Competência sem informação suficiente**

Se não existirem evidências suficientes para determinar um nível de competência, o perfil apresenta a competência sem atribuir um nível definitivo.

### Regras de Negócio e Restrições

- Uma unidade curricular pode contribuir para várias competências.
- Uma competência pode ser alimentada por várias unidades curriculares.
- Uma evidência não curricular pode estar associada a mais do que uma competência, quando justificável.
- O peso de contribuição de uma unidade curricular para uma competência é relativo à própria associação.
- O nível da competência deve considerar o número e o peso das evidências e, quando aplicável, as classificações obtidas.
- Evidências mais recentes ou diretamente relacionadas podem ter maior peso, embora a fórmula concreta permaneça em aberto.
- O histórico de associações e alterações não deve eliminar informação sobre perfis calculados anteriormente.

### Conceitos de Domínio Revelados

- **Competência**
- **Nível de Competência**
- **Associação Competência–Unidade Curricular**
- **Peso/Grau de Contribuição**
- **Evidência**
- **Validação de Evidência**
- **Grau de Confiança**

---

## Estrutura do Perfil e Evidência

O perfil do estudante deve reunir duas áreas principais:

### Informação académica

A informação académica é baseada na **Trajetória Académica** e deve permitir compreender o percurso do estudante, incluindo:

- unidades curriculares;
- classificações;
- estados de conclusão;
- número de tentativas;
- anos letivos;
- progressão no curso.

As tentativas anteriores devem permanecer representadas no histórico, mesmo quando existe uma tentativa posterior com aprovação. A trajetória atual resulta desse histórico e não de um único resultado que substitua os anteriores.

### Informação de competências

A área de competências apresenta as competências relevantes para o estudante e as evidências que contribuem para cada uma.

As principais fontes de evidência são:

- **Evidência curricular:** unidades curriculares concluídas que possuem associações a competências;
- **Evidência não curricular:** projetos, atividades extracurriculares, certificações externas ou outras evidências submetidas pelo estudante.

Cada associação entre uma unidade curricular e uma competência possui um grau/peso de contribuição. As evidências não curriculares podem estar sujeitas a validação e possuir um grau de confiança diferente.

A escala de competências proposta inicialmente é de quatro níveis: **Iniciante, Em Desenvolvimento, Consolidado e Avançado**. Esta escala e a forma exata de cálculo do nível permanecem sujeitas a evolução.

---

## O que cada ator vê

### Estudante

O estudante pode consultar o seu próprio perfil, incluindo:

- trajetória e progressão académica;
- unidades curriculares e respetivos resultados;
- competências associadas ao seu percurso;
- evidências que contribuem para cada competência;
- evidências não curriculares submetidas e o respetivo estado de validação, quando aplicável.

O estudante não pode criar ou alterar competências do catálogo nem alterar os pesos das associações entre competências e unidades curriculares.

### Docente / Coordenador de curso / Serviços Académicos

O acesso destes atores depende das permissões definidas para a plataforma.

Quando autorizado, o ator pode consultar a informação do perfil de um estudante relevante para o seu contexto, incluindo a trajetória académica e as competências desenvolvidas.

Os atores com responsabilidades de gestão de competências podem também gerir o catálogo e as associações entre competências e unidades curriculares. Esta responsabilidade pertence ao domínio do Sistema de Competências e não à consulta do perfil.

---

## Alinhamento com outros Casos de Uso

O **Perfil do Estudante** depende conceptualmente dos casos de uso definidos para a **Trajetória Académica** e para o **Sistema de Competências**.

A trajetória académica fornece o histórico de inscrições, tentativas, classificações, estados de conclusão e progressão no curso. As tentativas são mantidas individualmente e organizadas cronologicamente por ano letivo.

O sistema de competências fornece as competências, as associações entre competências e unidades curriculares e as evidências não curriculares. Uma competência pode receber contributos de várias unidades curriculares e evidências.

Assim, o perfil funciona como uma visão consolidada destas duas áreas, sem substituir os seus respetivos dados ou regras.

---

## Questões em Aberto e Assunções

- As permissões exatas de **Docente / Serviços Académicos** para consultar perfis dependem da decisão da equipa sobre os atores e permissões.
- Está em aberto se todas as evidências não curriculares exigem validação humana ou se determinados tipos de evidência poderão ser automaticamente validados.
- Está em aberto se a escala de quatro níveis de competência será suficiente para todos os contextos.
- A fórmula exata para calcular o nível de uma competência ainda não está definida.
- Está em aberto como representar a evolução de uma competência ao longo do tempo, incluindo a possibilidade de alteração do nível quando uma competência deixa de ser utilizada.
- A forma exata de representar creditações ou equivalências de unidades curriculares permanece fora do âmbito do Sprint 1.
- Assume-se que o perfil apresenta informação derivada da trajetória académica e das evidências de competências, não sendo criado um novo histórico independente.
