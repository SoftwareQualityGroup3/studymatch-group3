# Base de dados: arranque local

O StudyMatch usa **MySQL 8.4 LTS**. As migrações são geridas com **Flyway** e encontram-se em:

```text
backend/src/main/resources/db/migration
```

## Instalar o MySQL

Instalar o **MySQL Community Server 8.4 LTS** e mantê-lo a correr como serviço.

Utilizar, por defeito:

- Host: `localhost`
- Porta: `3306`
- Utilizador administrativo: `root`

Definir uma password para o utilizador `root` durante a instalação.

## Criar as bases de dados e o utilizador

Abrir o MySQL Command Line Client ou outro cliente SQL como `root`:

```sql
CREATE DATABASE studymatch
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE DATABASE studymatch_test
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE USER 'studymatch'@'localhost'
    IDENTIFIED BY '<password>';

GRANT ALL PRIVILEGES ON studymatch.* TO 'studymatch'@'localhost';
GRANT ALL PRIVILEGES ON studymatch_test.* TO 'studymatch'@'localhost';

FLUSH PRIVILEGES;
```

A base `studymatch` é utilizada durante o desenvolvimento. A base `studymatch_test` fica disponível para os testes de integração e persistência.

## Configurar as variáveis de ambiente

Copiar o ficheiro de exemplo:

```bash
cp .env.example .env
```

Editar o `.env` e definir os valores utilizados no MySQL:

```dotenv
DB_HOST=localhost
DB_PORT=3306
DB_NAME=studymatch
DB_USER=studymatch
DB_PASSWORD=<password>
```

O Spring Boot não lê o ficheiro `.env` automaticamente. As variáveis devem existir no terminal ou na configuração de execução do IntelliJ antes de iniciar o backend.

### Linux/macOS/Git Bash

Na raiz do projeto:

```bash
set -a
source .env
set +a

cd backend
./mvnw spring-boot:run
```

### Windows PowerShell

Na raiz do projeto:

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([^#=]+)=(.*)$') {
        Set-Item -Path "Env:$($matches[1].Trim())" -Value $matches[2].Trim()
    }
}

cd backend
.\mvnw.cmd spring-boot:run
```

### IntelliJ

Em **Run → Edit Configurations**, abrir a configuração de `StudyMatchApplication` e definir:

```text
DB_HOST=localhost;DB_PORT=3306;DB_NAME=studymatch;DB_USER=studymatch;DB_PASSWORD=<password>
```

Se aparecer:

```text
Could not resolve placeholder 'DB_USER'
```

significa que as variáveis de ambiente não estavam definidas quando o backend foi iniciado.

## Verificar a migração

Ao arrancar o backend pela primeira vez, confirmar nos logs que o Flyway aplicou:

```text
V1__init.sql
```

Também é possível verificar a tabela técnica diretamente no MySQL:

```sql
USE studymatch;

SELECT * FROM app_status;
```

Deve existir uma linha com:

```text
status = UP
```

Quando a issue #22 estiver integrada, o endpoint de health-check pode ser testado com:

```bash
curl -i http://localhost:8080/api/health
```

A resposta esperada é:

```json
{
  "status": "UP",
  "database": "UP"
}
```

## Testar com a base de testes

Para utilizar a base de testes, definir temporariamente:

```bash
export DB_NAME=studymatch_test
```

Depois executar os testes do backend:

```bash
cd backend
./mvnw clean test
```

No Windows PowerShell:

```powershell
$env:DB_NAME = "studymatch_test"
cd backend
.\mvnw.cmd clean test
```

## Problemas comuns

| Sintoma | Causa provável |
|---|---|
| `Can't connect to MySQL server` | O serviço MySQL não está a correr |
| `Access denied for user` | O utilizador ou a password estão incorretos |
| `Unknown database 'studymatch'` | A base de dados ainda não foi criada |
| `Could not resolve placeholder 'DB_USER'` | As variáveis de ambiente não foram carregadas |
| Porta `3306` ocupada | Existe outra instância MySQL a utilizar a mesma porta |