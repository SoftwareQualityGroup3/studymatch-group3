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

---

## 2. Equipa

- **Ricardo Silva**
- **Guilherme Ribeiro**
- **João Xavier**
- **Alexandre Veiga**

---

## 3. Stack Tecnológica

- **Backend:** Java 21, Spring Boot (Web, Data JPA), Flyway, MySQL 8.4 LTS
- **Frontend:** React, Vite, Vitest (`frontend/`)
- **Base de Dados:** MySQL / MySQL Test (`studymatch`, `studymatch_test`)

---

## 4. Pré-requisitos

Certifica-te de que tens instalado no teu sistema:
1. **Java Development Kit (JDK) 21**
2. **Node.js** (versão LTS recomendada) e **npm**
3. **MySQL Server 8.4 LTS**

---

## 5. Instruções de Setup e Execução (Clone Limpo)

### Passo 1: Configurar a Base de Dados (MySQL)
Executa na tua consola MySQL os comandos para criar as bases de dados e o utilizador:
```sql
DROP DATABASE IF EXISTS studymatch;
DROP DATABASE IF EXISTS studymatch_test;
CREATE DATABASE studymatch CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE studymatch_test CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'studymatch'@'localhost' IDENTIFIED WITH mysql_native_password BY 'ricardosilva2006';
GRANT ALL PRIVILEGES ON studymatch.* TO 'studymatch'@'localhost';
GRANT ALL PRIVILEGES ON studymatch_test.* TO 'studymatch'@'localhost';
FLUSH PRIVILEGES;
