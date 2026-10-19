# StudyMatch

Plataforma inteligente de apoio à gestão da identidade académica, trajetória, perfil de competências e formação otimizada de grupos de estudantes.

---

## 1. Contexto e Objetivo do Produto

O **StudyMatch** é uma aplicação desenvolvida no âmbito académico com o objetivo de acompanhar o percurso dos estudantes, consolidar as suas competências (através de evidências curriculares e não curriculares) e auxiliar docentes na criação de contextos de agrupamento dinâmicos e equilibrados.

Principais áreas funcionais:
- **Identidade Académica e Contexto de Curso** (`docs/use-cases/identidade-estudante.md`)
- **Trajetória Académica** (`docs/use-cases/trajetoria-academica.md`)
- **Perfil do Estudante e Competências** (`docs/use-cases/perfil-estudante.md`, `docs/use-cases/competencias.md`)
- **Estratégia de Cold Start** (`docs/use-cases/cold-start.md`)
- **Contexto de Agrupamento** (`docs/use-cases/contexto-agrupamento.md`)

### Âmbito do Sprint 2 (Student Profile & Cold Start)

O Sprint 2 entrega uma fatia vertical funcional **Frontend → API → Lógica de negócio → Persistência**:

- **Estudantes:** criar, listar, consultar e atualizar, com pesquisa por texto (`q`) e filtro por curso (`course`) na API.
- **Histórico académico:** resultados por unidade curricular, com ano letivo e semestre.
- **Perfil de competências:** calculado a partir das notas, com pontuação (0–20), número de evidências e origem.
- **Cold start:** autoavaliação de 1 a 5 por competência, cuja influência diminui à medida que surgem notas.

A formação automática de grupos está fora do âmbito deste sprint. As decisões de design (modelo, fórmula do perfil, cold start) estão em [`docs/decisoes-sprint2.md`](docs/decisoes-sprint2.md).

---

## 2. Equipa

- **Ricardo Silva**
- **Guilherme Ribeiro**
- **João Xavier**
- **Alexandre Veiga**

---

## 3. Stack Tecnológica

- **Backend:** Java 21, Spring Boot 4.1.1 (Web, Data JPA, Validation), Flyway, MySQL 8.4 LTS, build com Maven Wrapper
- **Frontend:** React, Vite, Vitest (`frontend/`)
- **Base de Dados:** MySQL local, com duas bases: `studymatch` (desenvolvimento) e `studymatch_test` (testes)
- **Testes:** JUnit 5, Mockito e MockMvc no backend; Vitest e React Testing Library no frontend

---

## 4. Pré-requisitos

| Ferramenta | Versão |
| --- | --- |
| Java (JDK) | 21 |
| Node.js | 20 ou superior (LTS recomendada), com `npm` |
| MySQL Community Server | 8.4 LTS, a correr em `localhost:3306` |

O Maven **não** precisa de ser instalado: o projeto inclui o Maven Wrapper (`backend/mvnw` e `backend/mvnw.cmd`), que descarrega a versão certa na primeira execução (é preciso ligação à internet).

---

## 5. Instruções de Setup e Execução (Clone Limpo)

```bash
git clone https://github.com/SoftwareQualityGroup3/studymatch-group3.git
cd studymatch-group3
```

### Passo 1: Criar as bases de dados e o utilizador (MySQL)

Abrir o MySQL Command Line Client (ou outro cliente SQL) como `root` e executar, trocando `<password>` por uma password à escolha:

```sql
CREATE DATABASE IF NOT EXISTS studymatch CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS studymatch_test CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'studymatch'@'localhost' IDENTIFIED BY '<password>';
GRANT ALL PRIVILEGES ON studymatch.* TO 'studymatch'@'localhost';
GRANT ALL PRIVILEGES ON studymatch_test.* TO 'studymatch'@'localhost';
FLUSH PRIVILEGES;
```

Detalhes e resolução de problemas em [`docs/database-setup.md`](docs/database-setup.md).

> A password nunca é escrita no repositório. Fica apenas no ficheiro `.env` local (passo seguinte), que está no `.gitignore`.

### Passo 2: Configurar as variáveis de ambiente

Na raiz do projeto:

```bash
cp .env.example .env
```

Editar o `.env` e trocar `DB_PASSWORD` pela password do passo 1. Os restantes valores já estão certos para a configuração acima.

### Passo 3: Carregar as variáveis no terminal

As variáveis têm de existir no terminal onde se corre o backend e os testes. Na raiz do projeto:

**Linux / macOS / Git Bash**

```bash
set -a; source .env; set +a
```

**Windows PowerShell**

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([^#=]+)=(.*)$') {
        Set-Item -Path "Env:$($matches[1].Trim())" -Value $matches[2].Trim()
    }
}
```

> Estas variáveis só valem para o terminal atual. Num terminal novo, repetir este passo.

### Passo 4: Compilar e testar o backend

O `StudyMatchApplicationTest` arranca o Spring e liga-se à base de dados, por isso os testes devem correr contra a base de **testes**. Substituir `DB_NAME` só para este comando:

**Linux / macOS / Git Bash**

```bash
cd backend
DB_NAME=studymatch_test ./mvnw clean verify
```

**Windows PowerShell**

```powershell
cd backend
$env:DB_NAME = "studymatch_test"
.\mvnw.cmd clean verify
$env:DB_NAME = "studymatch"
```

O `clean verify` descarrega as dependências, compila, executa os testes unitários (JUnit 5) e produz o artefacto. Termina com `BUILD SUCCESS` e gera:

```text
backend/target/studymatch-backend-0.0.1-SNAPSHOT.jar
```

### Passo 5: Correr o backend (terminal 1)

Com as variáveis carregadas (passo 3) e a apontar para a base de desenvolvimento (`DB_NAME=studymatch`):

```bash
cd backend
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run
```

No primeiro arranque o Flyway cria as tabelas e carrega as unidades curriculares (migrações `V1` a `V5`). O backend fica em `http://localhost:8080`. Para confirmar:

```bash
curl -i http://localhost:8080/api/health
```

Resposta esperada (`200`): `{ "status": "UP", "database": "UP" }`.

Em alternativa, é possível arrancar o artefacto gerado no passo 4, a partir da pasta `backend/`:

```bash
java -jar target/studymatch-backend-0.0.1-SNAPSHOT.jar
```

### Passo 6: Correr o frontend (terminal 2)

```bash
cd frontend
npm ci
npm run dev
```

Abrir <http://localhost:5173>. O Vite reencaminha os pedidos `/api` para `localhost:8080`, por isso o backend tem de estar a correr.

### Passo 7: Testar e compilar o frontend

```bash
cd frontend
npm test            # testes (Vitest)
npm run build       # build de produção, gera frontend/dist/
```

---

## 6. Resumo dos Comandos

| O quê | Pasta | Comando |
| --- | --- | --- |
| Build + testes + artefacto do backend | `backend/` | `DB_NAME=studymatch_test ./mvnw clean verify` |
| Correr o backend | `backend/` | `./mvnw spring-boot:run` |
| Instalar dependências do frontend | `frontend/` | `npm ci` |
| Correr o frontend | `frontend/` | `npm run dev` |
| Testes do frontend | `frontend/` | `npm test` |
| Build do frontend | `frontend/` | `npm run build` |

No Windows, usar `.\mvnw.cmd` em vez de `./mvnw`.

---

## 7. Problemas Comuns

| Sintoma | Causa provável |
| --- | --- |
| `Could not resolve placeholder 'DB_USER'` | Variáveis não carregadas neste terminal (passo 3) |
| `Can't connect to MySQL server` | Serviço MySQL parado |
| `Access denied for user` | Utilizador ou password incorretos no `.env` |
| `Unknown database 'studymatch_test'` | Base de testes ainda não criada (passo 1) |
| `StudyMatchApplicationTest` falha com erro de ligação | MySQL parado ou variáveis em falta; este é o único teste que precisa de base de dados |
| `Permission denied` ao correr `./mvnw` | Falta a permissão de execução: `chmod +x backend/mvnw` |
| Frontend mostra erros ao carregar dados | Backend não está a correr |

Mais detalhe em [`docs/readme-como-executar.md`](docs/readme-como-executar.md).

---

## 8. Documentação

- [`docs/readme-como-executar.md`](docs/readme-como-executar.md) — guia de execução detalhado
- [`docs/database-setup.md`](docs/database-setup.md) — base de dados e variáveis de ambiente
- [`docs/decisoes-sprint2.md`](docs/decisoes-sprint2.md) — decisões de design do Sprint 2
- [`docs/architecture.md`](docs/architecture.md) e [`docs/tech-stack.md`](docs/tech-stack.md) — arquitetura e stack
- [`CONTRIBUTING.md`](CONTRIBUTING.md) e [`DevelopmentWorkflow.md`](DevelopmentWorkflow.md) — processo de trabalho (Issue → Branch → PR → Review → Merge)
