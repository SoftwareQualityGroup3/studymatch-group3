# Base de dados: arranque local

O StudyMatch usa **MySQL 8.4 LTS** (a série 8.0 chegou ao fim de suporte em abril de 2026). As migrações são geridas com **Flyway** (`backend/src/main/resources/db/migration`).

## Opção A: Docker (recomendada)

```bash
cp .env.example .env        # editar as passwords
docker compose up -d db
docker compose ps           # esperar pelo estado "healthy"
```

Parar: `docker compose down` (mantém os dados). Apagar também os dados: `docker compose down -v`.

## Opção B: MySQL local (sem Docker)

1. Instalar o **MySQL Community Server 8.4 LTS** (MySQL Installer para Windows, tipo "Server only"). Manter a porta `3306`, definir a password de `root` e deixar o MySQL a correr como serviço.
2. Abrir o *MySQL Command Line Client* (ou outro cliente SQL) como `root` e correr:

```sql
CREATE DATABASE studymatch CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER 'studymatch'@'localhost' IDENTIFIED BY '<password>';
GRANT ALL PRIVILEGES ON studymatch.* TO 'studymatch'@'localhost';
```

3. Copiar `.env.example` para `.env` e usar os mesmos valores (`DB_USER=studymatch`, `DB_PASSWORD=<password>`, `DB_NAME=studymatch`, `DB_PORT=3306`).
4. Seguir a secção seguinte. Para verificar a tabela, usar o cliente SQL: `USE studymatch; SELECT * FROM app_status;`.

## Variáveis de ambiente do backend

`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (ver `.env.example`).
O Spring Boot **não lê o `.env` sozinho**: as variáveis têm de existir no terminal (ou na configuração de execução do IntelliJ) antes de arrancar o backend.

**Windows (PowerShell), na raiz do projeto:**

```powershell
Get-Content .env | ForEach-Object { if ($_ -match '^\s*([^#=]+)=(.*)$') { Set-Item -Path "Env:$($matches[1].Trim())" -Value $matches[2].Trim() } }
cd backend
.\mvnw.cmd spring-boot:run
```

**Linux/macOS/Git Bash:**

```bash
set -a; source .env; set +a
cd backend && ./mvnw spring-boot:run
```

**IntelliJ:** *Run → Edit Configurations →* configuração de `StudyMatchApplication` → *Environment variables*, por exemplo
`DB_HOST=localhost;DB_PORT=3306;DB_NAME=studymatch;DB_USER=studymatch;DB_PASSWORD=<password>`.

Se aparecer `Could not resolve placeholder 'DB_USER'`, as variáveis não estavam definidas no momento do arranque.

## Verificar a migração

Confirmar nos logs do arranque do backend que o Flyway aplicou `V1__init.sql`. Quando a #22 estiver integrada, `curl http://localhost:8080/api/health` deve devolver `{"status":"UP","database":"UP"}`.

Confirmar a tabela técnica na BD:

```bash
docker exec -it studymatch-db mysql -u <DB_USER> -p <DB_NAME> -e "SELECT * FROM app_status;"
```

Deve devolver uma linha com `status = UP`.

## Problemas comuns

| Sintoma | Causa provável |
|---|---|
| `docker compose` não encontra o daemon | Docker Desktop não está a correr |
| Porta 3306 ocupada | Há um MySQL local a correr; mudar `DB_PORT` no `.env` (ex.: `3307`) |
| `Access denied for user` | Passwords do `.env` diferentes das usadas ao criar o volume; `docker compose down -v` e voltar a subir |
