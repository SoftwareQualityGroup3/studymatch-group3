## Como executar

Guia detalhado para compilar, testar e correr o StudyMatch. O resumo rápido está no [`README.md`](../README.md); os comandos aqui são os mesmos.

A aplicação cobre a cadeia **Frontend → API → Serviço → Repositório → MySQL**: o estudante, o histórico académico e a autoavaliação ficam guardados na base de dados, e o perfil é calculado a partir deles quando é pedido.

### Pré-requisitos

| Ferramenta | Versão |
|------------|--------|
| Java (JDK) | 21 |
| Node.js | 20 ou superior |
| MySQL Community Server | 8.4 LTS (serviço a correr em `localhost:3306`) |

O backend usa o Maven Wrapper (`backend/mvnw`), por isso não é preciso instalar o Maven. Na primeira execução o wrapper descarrega o Maven, e o Maven descarrega as dependências, pelo que é necessária ligação à internet.

### 1. Base de dados

Criar as bases de dados e o utilizador seguindo [`database-setup.md`](database-setup.md). Em resumo, como `root`:

```sql
CREATE DATABASE IF NOT EXISTS studymatch CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS studymatch_test CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'studymatch'@'localhost' IDENTIFIED BY '<password>';
GRANT ALL PRIVILEGES ON studymatch.* TO 'studymatch'@'localhost';
GRANT ALL PRIVILEGES ON studymatch_test.* TO 'studymatch'@'localhost';
FLUSH PRIVILEGES;
```

`studymatch` é a base de desenvolvimento e `studymatch_test` é a dos testes. A password nunca se escreve no repositório.

### 2. Variáveis de ambiente

Na raiz do projeto:

```bash
cp .env.example .env
```

Editar o `.env` e definir `DB_PASSWORD`. O ficheiro `.env` nunca deve ser commitado (está no `.gitignore`).

### 3. Carregar as variáveis no terminal

Na raiz do projeto, no mesmo terminal onde vai correr o backend e os testes.

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

O `application.properties` também tenta importar `../.env` quando o backend arranca a partir de `backend/`, mas carregar as variáveis no terminal funciona sempre e tem prioridade sobre o ficheiro. No IntelliJ, definir as variáveis em **Run → Edit Configurations** (ver [`database-setup.md`](database-setup.md)).

### 4. Build e testes do backend

O `StudyMatchApplicationTest` precisa de base de dados, por isso o build corre contra `studymatch_test`.

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

O comando resolve as dependências, compila, executa os testes unitários (JUnit 5) e produz o artefacto:

```text
backend/target/studymatch-backend-0.0.1-SNAPSHOT.jar
```

Só o `StudyMatchApplicationTest` precisa de MySQL; os restantes testes do backend são unitários e correm sem base de dados.

### 5. Backend (terminal 1)

Com as variáveis carregadas e `DB_NAME=studymatch`:

```bash
cd backend
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run
```

No primeiro arranque, o Flyway aplica as migrações `V1` a `V5` (tabelas, unidades curriculares de exemplo e autoavaliação). O backend fica em `http://localhost:8080`.

Alternativa, usando o artefacto do passo 4 (a partir de `backend/`):

```bash
java -jar target/studymatch-backend-0.0.1-SNAPSHOT.jar
```

Verificar:

```bash
curl -i http://localhost:8080/api/health
```

Resposta esperada (`200`):

```json
{ "status": "UP", "database": "UP" }
```

### 6. Frontend (terminal 2)

```bash
cd frontend
npm ci
npm run dev
```

Abrir <http://localhost:5173>. O Vite redireciona `/api` para `localhost:8080`.

Testes e build do frontend:

```bash
npm test
npm run build
```

### 7. Demonstração dos cenários do Sprint 2

Passos para mostrar no Sprint Review, com o backend e o frontend a correr. As unidades curriculares de exemplo (`PROG1`, `WEB`, `ALGA`, `BD`, `ES`, `CALC`) vêm da migração `V4`.

**Cenário A: estudante com histórico académico**

1. No frontend, criar um estudante (**Novo estudante**) e abrir o detalhe. Anotar o `id` (aparece no endereço, `/students/<id>`).
2. Registar duas notas (substituir `<id>`):

```bash
curl -X POST http://localhost:8080/api/students/<id>/records -H "Content-Type: application/json" \
  -d '{"courseUnitCode":"PROG1","grade":16,"academicYear":"2024/25","semester":1}'
curl -X POST http://localhost:8080/api/students/<id>/records -H "Content-Type: application/json" \
  -d '{"courseUnitCode":"WEB","grade":12,"academicYear":"2024/25","semester":2}'
```

3. Recarregar o detalhe. Em **Programação**, a pontuação é a média ponderada (16×1 + 12×0,8) / 1,8 ≈ **14.22**, com 2 evidências e origem "Notas".

**Cenário B: cold start**

1. Criar outro estudante, sem notas. O perfil mostra **"Sem dados"** nas quatro competências (nunca 0).
2. Preencher a **Autoavaliação** (por exemplo 3, 4, 2 e 5) e guardar. O perfil atualiza sozinho para **12.00, 16.00, 8.00 e 20.00**, com 0 evidências e origem "Autoavaliação" (nota × 4).
3. Voltar ao estudante do cenário A, guardar a autoavaliação com Programação = 5 e ver a transição: a pontuação passa a ≈ **15.38**, origem "Notas e autoavaliação". A autoavaliação pesa 0,5 / (n + 0,5), por isso influencia menos à medida que há mais notas.

**Cenário C: funcionalidade da equipa (pesquisa e filtro)**

A API de estudantes aceita pesquisa por texto e filtro por curso (substituir `<curso>` por um curso existente, por exemplo `LEI`):

```bash
curl "http://localhost:8080/api/students?q=ana"
curl "http://localhost:8080/api/students?course=<curso>"
```

**Cenário D: testes e build**

```bash
cd backend && DB_NAME=studymatch_test ./mvnw clean verify
cd frontend && npm test
```

### Endpoints principais

| Método | Caminho | Função |
|--------|---------|--------|
| `GET` | `/api/health` | Estado da aplicação e da base de dados |
| `POST` | `/api/students` | Criar estudante |
| `GET` | `/api/students` | Listar, com pesquisa e filtro opcionais |
| `GET`, `PUT` | `/api/students/{id}` | Consultar e atualizar estudante |
| `GET` | `/api/students/{id}/profile` | Perfil de competências calculado |
| `POST`, `GET` | `/api/students/{id}/records` | Registar e listar notas |
| `PUT`, `GET` | `/api/students/{id}/self-assessment` | Guardar e consultar a autoavaliação |
| `GET` | `/api/course-units` | Listar unidades curriculares |

### Problemas comuns

| Sintoma | Causa provável |
|---------|----------------|
| `Could not resolve placeholder 'DB_USER'` | Variáveis não carregadas no terminal (passo 3) |
| `Can't connect to MySQL server` | Serviço MySQL parado |
| `Access denied for user` | Utilizador ou password incorretos |
| `Unknown database 'studymatch'` ou `'studymatch_test'` | Base de dados ainda não criada |
| `StudyMatchApplicationTest` falha | MySQL parado, variáveis em falta ou `DB_NAME` incorreto; é o único teste que usa a base de dados |
| `Permission denied` ao correr `./mvnw` | Falta a permissão de execução: `chmod +x backend/mvnw` |
| Frontend não carrega dados | Backend não está a correr |
| Backend sobe mas o `health` mostra `DOWN` | Backend a correr sem ligação à base de dados |
