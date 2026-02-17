# Security Test Project

A multi-module Spring Boot backend showcasing:
- username/password login
- JWT authorization for protected endpoints
- role-based access control
- layered architecture (`web -> use-case -> gateway -> repository`)

The project has two modules:
- `auth-core`: reusable security module (JWT filters, security chain, handlers)
- `secure-app-demo`: application module (controllers, use cases, persistence, migrations)

---

## 1) Tech stack

- Java 21
- Maven 3.9+
- Spring Boot 4.0.2
- Spring Security 7
- PostgreSQL 15 (via Docker Compose)
- Flyway migrations
- JUnit 6 + Spring Test + Mockito

---

## 2) Repository structure

```text
SecurityTestProject/
  pom.xml                        # Parent pom (multi-module)
  auth-core/
    src/main/java/com/sdl/...
  secure-app-demo/
    docker-compose.yml
    pom.xml
    src/main/java/com/sdl/...
    src/main/resources/
      application.properties
      db/migration/*.sql
    src/test/java/com/sdl/...
```

---

## 3) Prerequisites

Install and verify:

```bash
java -version
mvn -version
docker --version
docker compose version
```

Expected:
- Java: `21.x`
- Maven: `3.x`
- Docker/Compose: any current stable release

---

## 4) Configuration used by default

From `secure-app-demo/src/main/resources/application.properties`:

- App port: `7020`
- DB URL: `jdbc:postgresql://localhost:5433/DemoAppDb`
- DB user: `SDL_Admin`
- DB password: `SDL_Admin`
- Flyway: enabled
- JWT secret property key: `jwt.secret`

Important:
- JWT secret must be long enough for HS256 (at least 32 bytes).
- Use a strong non-default secret in real environments.

---

## 5) Start infrastructure (PostgreSQL + pgAdmin)

From project root:

```bash
cd secure-app-demo
docker compose up -d
```

Services:
- PostgreSQL
  - host: `localhost`
  - port: `5433`
  - db: `DemoAppDb`
  - user/password: `SDL_Admin` / `SDL_Admin`
- pgAdmin
  - URL: `http://localhost:5050`
  - user/password: `admin@demoApp.com` / `admin`

Useful commands:

```bash
docker compose ps
docker compose logs -f postgres
docker compose down
```

---

## 6) Build and run the application

### Build all modules

From root:

```bash
mvn clean install
```

### Run demo app

From root:

```bash
mvn -pl secure-app-demo spring-boot:run
```

App URL:
- `http://localhost:7020`

---

## 7) Database migrations

Flyway scripts are in:
- `secure-app-demo/src/main/resources/db/migration`

Migration coverage:
- `V1__CREATE_SCHEMA.sql`: tables (`users`, `roles`, `authorities`, `books`, join tables)
- `V2__ROLE_AND_AUTHORITIES_INSERT.sql`: seed roles/authorities + sample books
- `V3__CREATE_VIEWS_BOOK_USER.sql`: `book_view` and `user_view`

Flyway runs automatically on application startup.

---

## 8) Authentication and authorization model

### Authentication

- Login endpoint: `POST /api/onboarding/login`
- Request body contains `username` and `password`
- On success, response includes JWT token (`accessToken`)

### Authorization

- Clients send: `Authorization: Bearer <token>`
- `JWTTokenValidatorFilter` validates token and populates security context
- Endpoint access is enforced by Spring Security + method security

### Basic authentication status

- Basic auth is disabled.
- Sending `Authorization: Basic ...` is rejected with `401`.

---

## 9) API quickstart (copy/paste)

### Health check (public)

```bash
curl http://localhost:7020/api/public/health
```

### Register user

```bash
curl -X POST "http://localhost:7020/api/onboarding/register" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"demo_user\",\"password\":\"demo_password\",\"email\":\"demo_user@mail.com\"}"
```

### Login (username/password)

```bash
curl -X POST "http://localhost:7020/api/onboarding/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"demo_user\",\"password\":\"demo_password\"}"
```

Copy `data.accessToken` from the response.

### Call protected user endpoint

```bash
curl "http://localhost:7020/api/user/me" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### Call admin endpoint

```bash
curl "http://localhost:7020/api/admin/users" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

---

## 10) Endpoint summary

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/public/health` | Public |
| GET | `/api/public/health/liveness` | Public |
| GET | `/api/public/health/readiness` | Public |
| POST | `/api/onboarding/register` | Public |
| POST | `/api/onboarding/login` | Public |
| GET | `/api/user/me` | Authenticated |
| PUT | `/api/user/update/{id}` | Role `USER` |
| GET | `/api/admin/users` | Role `ADMIN` |

---

## 11) Testing

From root:

```bash
mvn test
```

Current Spring-based test coverage includes:
- authentication use-case tests (`ManagementUseCase`)
- application/book functionality tests (`ApplicationUseCase`)

Tests are located in:
- `secure-app-demo/src/test/java/com/sdl/application`

---

## 12) Security flow details

Security chain is defined in:
- `auth-core/src/main/java/com/sdl/config/SecurityConfiguration.java`

Main behavior:
- stateless session policy
- CSRF disabled (API style)
- form login disabled
- JWT validation filter applied before authorization checks
- custom `AuthenticationEntryPoint` and `AccessDeniedHandler`

Token generation:
- `ApplicationTokenProvider` creates JWT in login flow
- claims include `username` and `authorities`

---

## 13) Troubleshooting

### App cannot connect to database

- ensure `docker compose up -d` is running in `secure-app-demo`
- verify PostgreSQL is on `5433`
- check logs:

```bash
cd secure-app-demo
docker compose logs -f postgres
```

### JWT-related startup/runtime errors

- ensure `jwt.secret` is present and sufficiently long (>= 32 bytes)
- avoid very short secrets in local overrides

### 401 on protected endpoints

- verify token is present in `Authorization: Bearer <token>`
- verify token is not expired
- verify you did not send `Basic` auth header

### 403 on admin endpoint

- token is valid but user lacks `ADMIN` role

---

## 14) Development workflow

Recommended cycle:

1. Start DB (`docker compose up -d`)
2. Run app (`mvn -pl secure-app-demo spring-boot:run`)
3. Run tests (`mvn test`)
4. Stop infra (`docker compose down`)

---
