# 💰 Meu Porquinho

Sistema de controle financeiro pessoal. Permite registrar entradas, gastos e
investimentos, acompanhar a evolução mês a mês, analisar gastos por categoria
e gerenciar o próprio perfil.

## Stack

| Camada     | Tecnologia |
|------------|------------|
| Backend    | Java 21, Spring Boot 4 (Web, Data JPA, Security, Validation), JWT (jjwt), Flyway, Lombok |
| Frontend   | Angular 22 (standalone components, signals), ng2-charts (Chart.js), RxJS, Vitest |
| Banco      | PostgreSQL 16 (via Docker), pgAdmin |
| Infra      | Docker Compose, Maven Wrapper, Angular CLI |

## Estrutura do repositório

```
meuPorquinho/
├── backend/porquinho/   # API REST Spring Boot
├── frontend/            # Aplicação Angular
├── init/                # Scripts executados na 1ª inicialização do Postgres
└── docker-compose.yml   # Postgres + pgAdmin
```

## Pré-requisitos

- [Docker](https://www.docker.com/) (para o banco de dados)
- Java 21+
- Node.js 20+ e npm

## Como rodar

### 0. Variáveis de ambiente

```bash
cp .env.example .env   # Windows: copy .env.example .env
```

Edite o `.env` e defina as senhas e o `JWT_SECRET` (gere um segredo forte com
`openssl rand -base64 48`). O `.env` nunca é commitado — está no `.gitignore`.

### 1. Banco de dados

```bash
docker compose up -d
```

Sobe o PostgreSQL em `localhost:5432` e o pgAdmin em <http://localhost:5050>,
usando as credenciais do seu `.env`.

### 2. Backend

```bash
export JWT_SECRET=<o-mesmo-valor-do-seu-.env>   # Windows: set JWT_SECRET=...
cd backend/porquinho
./mvnw spring-boot:run    # Windows: mvnw.cmd spring-boot:run
```

A API sobe em <http://localhost:8080>. Sem `JWT_SECRET` definido, a aplicação
não inicia (falha rápida de propósito, para nunca rodar com segredo padrão).
O Flyway aplica as migrations de `src/main/resources/db/migration`
automaticamente na primeira execução.

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

Aplicação em <http://localhost:4200>. Crie uma conta pela tela de registro e
faça login.

## Variáveis de ambiente

Todas as credenciais vivem no `.env` (não versionado). O versionado é apenas o
modelo [.env.example](.env.example). O backend lê as variáveis de ambiente do
sistema; o Docker Compose lê o `.env` automaticamente.

| Variável                   | Usada por       | Descrição                                      |
|----------------------------|-----------------|------------------------------------------------|
| `JWT_SECRET`               | Backend         | Chave HMAC (Base64) para assinar tokens — **obrigatória** |
| `JWT_EXPIRATION`           | Backend         | Validade do token em ms (padrão: `3600000` = 1h) |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | Backend | Conexão com o Postgres (padrões apontam para o Docker local) |
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | Docker Compose | Credenciais do banco |
| `PGADMIN_DEFAULT_EMAIL` / `PGADMIN_DEFAULT_PASSWORD` | Docker Compose | Login do pgAdmin |

## Endpoints da API

Base URL: `http://localhost:8080`

### Autenticação (públicos)

| Método | Rota                  | Descrição            |
|--------|-----------------------|----------------------|
| POST   | `/api/auth/login`     | Login (retorna JWT)  |
| POST   | `/api/auth/registro`  | Criar conta          |

### Lançamentos (autenticados — header `Authorization: Bearer <token>`)

| Método | Rota                          | Descrição                                        |
|--------|-------------------------------|--------------------------------------------------|
| GET    | `/api/lancamentos`            | Lista paginada. Filtros: `tipo`, `categoria`, `mesAno` (yyyy-MM), `page`, `size` |
| POST   | `/api/lancamentos`            | Cria lançamento (`GASTO`, `ENTRADA` ou `INVESTIMENTO`) |
| DELETE | `/api/lancamentos/{id}`       | Remove lançamento do usuário logado              |

### Resumos

| Método | Rota                              | Descrição                              |
|--------|-----------------------------------|----------------------------------------|
| GET    | `/api/resumo`                     | Totais do mês (`mesAno` opcional)      |
| GET    | `/api/resumo/categorias`          | Gastos agrupados por categoria         |
| GET    | `/api/resumo/evolucao?meses=6`    | Evolução mês a mês                     |
| GET    | `/api/resumo/comparativo`         | Compara dois meses (`mesAtual`, `mesAnterior`) |

### Usuário

| Método | Rota                            | Descrição                    |
|--------|---------------------------------|------------------------------|
| GET    | `/api/usuarios/me`              | Perfil do usuário logado     |
| GET    | `/api/usuarios/me/estatisticas` | Estatísticas da conta        |
| PUT    | `/api/usuarios/me`              | Atualiza nome/e-mail         |
| PATCH  | `/api/usuarios/me/senha`        | Altera a senha               |
| DELETE | `/api/usuarios/me?senha=...`    | Exclui a conta               |

## Testes

```bash
# Backend
cd backend/porquinho && ./mvnw test

# Frontend
cd frontend && npm test
```

## Build de produção (frontend)

```bash
cd frontend && npm run build
```

Artefatos gerados em `frontend/dist/`.

## Licença

[MIT](LICENSE)
