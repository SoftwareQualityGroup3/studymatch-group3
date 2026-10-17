# Modelo de Domínio — Diagrama de Classes

Este documento apresenta o diagrama de classes do domínio do StudyMatch, construído a partir dos conceitos, atributos, relações e cardinalidades definidos na issue #16.

O diagrama representa exclusivamente conceitos do domínio. Não inclui controllers, services, repositories, DTOs, endpoints, tabelas de base de dados ou outros elementos de implementação. A tabela técnica `app_status` também não faz parte do modelo.

```mermaid
classDiagram
direction LR

class Estudante {
    +nome
    +contactoInstitucional
    +estadoEstudante
    +dataRegisto
}

class IdentificadorInstitucional["Identificador Institucional"] {
    +valorUnico
    +tipoIdentificador
    +dataAtribuicao
}

class Curso {
    +codigo
    +nome
    +tipoCurso
}

class PlanoDeEstudos["Plano de Estudos"] {
    +designacao
    +versao
}

class ContextoDeCurso["Contexto de Curso"] {
    +anoLetivoIngresso
    +dataInicioVigencia
    +dataFimVigencia
}

class AnoCurricular["Ano Curricular"] {
    +numeroAno
    +dataInicio
    +dataFim
}

class TrajetoriaAcademica["Trajetória Académica"] {
    +anoLetivo
    +progressaoCurso
}

class UnidadeCurricular["Unidade Curricular"] {
    +codigo
    +nome
    +creditosECTS
    +anoLetivoOferta
    +estadoDisponibilidade
}

class Inscricao["Inscrição"] {
    +anoLetivo
    +estadoInscricao
}

class Tentativa {
    +numeroTentativa
    +epocaAvaliacao
    +dataTentativa
}

class Classificacao["Classificação"] {
    +valor
    +escalaAvaliacao
    +dataAtribuicao
}

class EstadoDeConclusao["Estado de Conclusão"] {
    +valorEstado
    +dataAtualizacao
}

class PerfilDoEstudante["Perfil do Estudante"] {
    +grauCompletude
    +dataUltimaAtualizacao
}

class Competencia["Competência"] {
    +nome
    +descricao
    +nivelCompetencia
}

class CategoriaDeCompetencia["Categoria de Competência"] {
    +nomeCategoria
    +descricao
}

class Evidencia["Evidência"] {
    +tipoEvidencia
    +descricao
    +origem
    +data
    +grauConfianca
    +estadoValidacao
}

class EvidenciaCurricular["Evidência Curricular"] {
    +classificacao
    +pesoContribuicao
    +dataAvaliacao
}

class EvidenciaNaoCurricular["Evidência Não Curricular"]

class Autoavaliacao["Autoavaliação"] {
    +nivelIndicado
    +dataResposta
    +origemEvidencia
}

class EstadoDoPerfil["Estado do Perfil"] {
    +valorEstado
    +dataUltimaAtualizacao
}

class ContextoDeAgrupamento["Contexto de Agrupamento"] {
    +estadoContexto
    +dataCriacao
}

class Atividade {
    +nome
    +descricao
    +prazo
}

class TamanhoAlvo["Tamanho Alvo"] {
    +valorFixo
    +minimo
    +maximo
    +unidade
}

class RestricaoDeAgrupamento["Restrição de Agrupamento"] {
    +tipoRestricao
    +descricao
    +nivelObrigatoriedade
}

%% =========================================
%% Especializações
%% =========================================

Evidencia <|-- EvidenciaCurricular
Evidencia <|-- EvidenciaNaoCurricular
EvidenciaNaoCurricular <|-- Autoavaliacao

%% =========================================
%% Identidade e contexto académico
%% =========================================

Estudante "1" -- "1" IdentificadorInstitucional
Estudante "1" -- "1..*" ContextoDeCurso

ContextoDeCurso "*" -- "1" Curso
ContextoDeCurso "*" -- "1" PlanoDeEstudos
ContextoDeCurso "*" -- "1" AnoCurricular

Curso "1" -- "1..*" PlanoDeEstudos
PlanoDeEstudos "1" -- "1..*" AnoCurricular
PlanoDeEstudos "*" -- "*" UnidadeCurricular

%% =========================================
%% Trajetória académica
%% =========================================

Estudante "1" -- "1" TrajetoriaAcademica

TrajetoriaAcademica "1" -- "0..*" Inscricao
Inscricao "*" -- "1" UnidadeCurricular
Inscricao "1" -- "0..*" Tentativa

Tentativa "1" -- "0..1" Classificacao
Tentativa "*" -- "1" EstadoDeConclusao

UnidadeCurricular "1" -- "0..*" Atividade

%% =========================================
%% Competências, evidências e perfil
%% =========================================

Competencia "*" -- "0..1" CategoriaDeCompetencia

UnidadeCurricular "*" -- "*" Competencia : contribui para

Estudante "1" -- "1" PerfilDoEstudante
PerfilDoEstudante "1" -- "1" EstadoDoPerfil
PerfilDoEstudante "*" -- "*" Competencia : deriva de evidências

Estudante "1" -- "0..*" Evidencia
Evidencia "*" -- "1..*" Competencia : sustenta

EvidenciaCurricular "*" -- "1" UnidadeCurricular

Autoavaliacao "*" -- "1" Estudante
Autoavaliacao "*" -- "1" Competencia

%% =========================================
%% Contexto de agrupamento
%% =========================================

ContextoDeAgrupamento "*" -- "1" UnidadeCurricular
ContextoDeAgrupamento "*" -- "0..1" Atividade

ContextoDeAgrupamento "1" -- "1" TamanhoAlvo
ContextoDeAgrupamento "1" -- "0..*" RestricaoDeAgrupamento
```

## Notas

- Os conceitos, atributos-chave, relações e cardinalidades seguem o modelo de domínio definido na issue #16.
- Os nomes apresentados no diagrama correspondem aos conceitos utilizados no modelo de domínio.
- As relações de especialização representam:
  - `Evidência Curricular` como especialização de `Evidência`;
  - `Evidência Não Curricular` como especialização de `Evidência`;
  - `Autoavaliação` como especialização de `Evidência Não Curricular`.
- O diagrama contém apenas elementos do domínio.
- A tabela técnica `app_status` não faz parte do modelo de domínio.
