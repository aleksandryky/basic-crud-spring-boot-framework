# basic-crud-spring-boot-framework

Spring Boot 3 REST CRUD API backed by **MySQL** and **Spring Data JPA**.

## Prerequisites

- JDK 17+
- MySQL 8+ running locally (or adjust the JDBC URL)

## Configuration

1. **Run MySQL** on port `3306` (or change the URL in `application.properties`).
2. **Credentials** — either edit `spring.datasource.username` / `spring.datasource.password` in `src/main/resources/application.properties`, or set environment variables (defaults: user `root`, empty password):
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`
3. **URL** — `spring.datasource.url` uses database `basic_crud`; `createDatabaseIfNotExist=true` creates it if missing.

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

## REST endpoints (`Product`)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/products` | List all |
| `GET` | `/api/products/{id}` | Get one |
| `POST` | `/api/products` | Create |
| `PUT` | `/api/products/{id}` | Update |
| `DELETE` | `/api/products/{id}` | Delete |

### Example: create

```bash
curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Notebook","description":"A5 lined","price":12.99}'
```

`name` is required; `price` must be zero or positive.

## Sample data

On startup, if the `products` table is empty, three sample rows are inserted (see `SampleProductDataLoader`).

## Postman

Import **`postman/Basic-CRUD-Products.postman_collection.json`** (Postman → Import → file). Collection variables: `baseUrl` (`http://localhost:8080`), `productId` (use an existing id from “List all products”).
