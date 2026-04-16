# basic-crud-spring-boot-framework

Spring Boot 3 REST CRUD API backed by **MySQL** and **Spring Data JPA**, with **JWT** authentication for use as a **Next.js** (or any) frontend backend.

## Prerequisites

- JDK 17+
- MySQL 8+ running locally (or adjust the JDBC URL)

## Configuration

1. **Run MySQL** on port `3306` (or change the URL in `application.properties`).
2. **Credentials** — either edit `spring.datasource.username` / `spring.datasource.password` in `src/main/resources/application.properties`, or set environment variables (defaults: user `root`, empty password):
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`
3. **URL** — `spring.datasource.url` uses database `basic_crud`; `createDatabaseIfNotExist=true` creates it if missing.

### Flyway migrations

SQL migrations are under **`src/main/resources/db/migration/`**. Version **`V1`** creates **`app_users`** (JWT-backed accounts) and **`products`**.

- **`spring.jpa.hibernate.ddl-auto=validate`** — Hibernate checks the schema but does not create or alter tables; Flyway owns DDL.
- **`spring.flyway.baseline-on-migrate=true`** — If the database already had tables but no `flyway_schema_history` (e.g. after using Hibernate `ddl-auto` before), Flyway records a baseline at version **1** and skips **`V1`** so existing tables are not recreated.

Run migrations without starting the app (defaults match `application.properties`: `root`, empty password, `basic_crud`):

```bash
./mvnw flyway:migrate -Dflyway.user=root -Dflyway.password=
```

Override URL or password when needed:

```bash
./mvnw flyway:migrate -Dflyway.url='jdbc:mysql://localhost:3306/basic_crud?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' -Dflyway.user=root -Dflyway.password=yourpassword
```

If Flyway reports a failed migration, run **`./mvnw flyway:repair`**, fix the script, then **`flyway:migrate`** again.

If the app fails with **`Schema-validation: missing table [app_users]`** while Flyway says the schema is already at version **1**, your DB was likely **baselined** when only `products` existed (older Hibernate setup). Pull the latest migrations and run again, or run **`./mvnw flyway:migrate`** so **`V2__Ensure_app_users`** applies. Use **`./mvnw clean spring-boot:run`** if an old `V2__Create_products.sql` remains under `target/classes`.

### JWT and CORS (Next.js)

In `application.properties` (or env):

| Property / env | Purpose |
|----------------|---------|
| `app.jwt.secret` / `JWT_SECRET` | Secret string (hashed to a 256-bit HMAC key). Use a long random value in production. |
| `app.jwt.expiration-ms` / `JWT_EXPIRATION_MS` | Access token lifetime (default 24h). |
| `app.cors.allowed-origins` / `CORS_ORIGINS` | Comma-separated browser origins (default `http://localhost:3000`). |

### Install MySQL locally (macOS, Homebrew)

```bash
brew install mysql
brew services start mysql
```

Secure or set a root password if your install requires it, then put that password in `application.properties` or run:

```bash
export MYSQL_PASSWORD='your-password'
./mvnw spring-boot:run
```

## Run

```bash
./mvnw spring-boot:run
```

The API listens on **http://localhost:8080**.

## Authentication (JWT)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/register` | Create user; returns `accessToken` (JSON body: `username`, `password`; password min 8 chars). |
| `POST` | `/api/auth/login` | Returns `accessToken` for existing user. |

Response shape:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresInSeconds": 86400
}
```

Send the token on protected requests:

```http
Authorization: Bearer <jwt>
```

All **`/api/products/**`** routes require a valid JWT. **`/api/auth/**`** is public.

### Example: register, then call API

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo_user","password":"password123"}' | jq -r .accessToken)

curl -s http://localhost:8080/api/products -H "Authorization: Bearer $TOKEN"
```

### Next.js usage

From the browser, call the API with the stored token (e.g. in memory or `sessionStorage`):

```ts
await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/products`, {
  headers: {
    Authorization: `Bearer ${accessToken}`,
    'Content-Type': 'application/json',
  },
});
```

Ensure `NEXT_PUBLIC_API_URL` matches your Spring origin (e.g. `http://localhost:8080`) and that `CORS_ORIGINS` includes your Next dev URL.

## REST endpoints (`Product`)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/products` | List all |
| `GET` | `/api/products/{id}` | Get one |
| `POST` | `/api/products` | Create |
| `PUT` | `/api/products/{id}` | Update |
| `DELETE` | `/api/products/{id}` | Delete |

### Example: create (with token)

```bash
curl -s -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Notebook","description":"A5 lined","price":12.99}'
```

`name` is required; `price` must be zero or positive.

## Sample data

On startup, if the `products` table is empty, three sample rows are inserted (see `SampleProductDataLoader`).

## Postman

Import **`postman/Basic-CRUD-Products.postman_collection.json`**. Run **Auth → Register** or **Login** first (tests save `accessToken`). Then use **Products** requests (they send `Authorization: Bearer {{accessToken}}`). Set `productId` from “List all products” when needed.
