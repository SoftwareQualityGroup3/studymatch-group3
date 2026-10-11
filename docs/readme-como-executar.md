## Como executar (vertical slice)

O vertical slice valida a cadeia **Frontend → API → Serviço → Repositório → MySQL**: o frontend chama `GET /api/health` e mostra o estado da aplicação e da base de dados.

### Pré-requisitos

| Ferramenta | Versão |
|------------|--------|
| Java (JDK) | 21 |
| Node.js | 20 ou superior |
| MySQL Community Server | 8.4 LTS (serviço a correr em `localhost:3306`) |

O backend usa o Maven Wrapper (`mvnw`), por isso não é preciso instalar o Maven.

### 1. Base de dados

Criar as bases de dados e o utilizador seguindo [`docs/database-setup.md`](docs/database-setup.md). Em resumo, como `root`:

```sql
CREATE DATABASE studymatch CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER 'studymatch'@'localhost' IDENTIFIED BY '<password>';
GRANT ALL PRIVILEGES ON studymatch.* TO 'studymatch'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Variáveis de ambiente

Na raiz do projeto:

```bash
cp .env.example .env
```

Editar o `.env` e definir `DB_PASSWORD`. O ficheiro `.env` nunca deve ser commitado.

### 3. Backend (terminal 1)

O Spring Boot não lê o `.env` sozinho: as variáveis têm de ser carregadas no mesmo terminal.

**Linux / macOS / Git Bash**

```bash
set -a; source .env; set +a
cd backend
./mvnw spring-boot:run
```

**Windows PowerShell**

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([^#=]+)=(.*)$') {
        Set-Item -Path "Env:$($matches[1].Trim())" -Value $matches[2].Trim()
    }
}
cd backend
.\mvnw.cmd spring-boot:run
```

No primeiro arranque, o Flyway aplica a migração `V1__init.sql`. O backend fica em `http://localhost:8080`.

Verificar:

```bash
curl -i http://localhost:8080/api/health
```

Resposta esperada (`200`):

```json
{ "status": "UP", "database": "UP" }
```

### 4. Frontend (terminal 2)

```bash
cd frontend
npm install
npm run dev
```

Abrir <http://localhost:5173>. A página mostra `Aplicação: UP` e `Base de dados: UP`. O Vite redireciona `/api` para `localhost:8080`.

### 5. Testes

```bash
# Backend
cd backend && ./mvnw clean test        # Windows: .\mvnw.cmd clean test

# Frontend
cd frontend && npm test
```

### Problemas comuns

| Sintoma | Causa provável |
|---------|----------------|
| `Could not resolve placeholder 'DB_USER'` | `.env` não carregado no terminal do backend |
| `Can't connect to MySQL server` | Serviço MySQL parado |
| `Access denied for user` | Utilizador ou password incorretos |
| `Unknown database 'studymatch'` | Base de dados ainda não criada |
| Página mostra "Não foi possível obter o estado da aplicação." | Backend não está a correr |
| Página mostra `DOWN` / `DOWN` | Backend a correr mas sem ligação à base de dados |
