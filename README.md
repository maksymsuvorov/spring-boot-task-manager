# TaskFlow

A task management REST API for small teams, built with **Spring Boot 4** as a hands-on learning project.

## Tech stack

- Java 21, Spring Boot 4.1 (Web MVC, Data JPA, Security, Validation)
- PostgreSQL 16, Flyway migrations
- JWT authentication (jjwt), BCrypt password hashing
- Gradle

## Features

- CRUD for users, projects, and tasks with a DTO layer and bean validation
- Stateless JWT authentication with short-lived access tokens (15 min)
- Rotating, single-use refresh tokens stored server-side (SHA-256 hashed)
- Role-based access control (`USER` / `ADMIN`) via method security
- Resource ownership checks (only a project's owner can modify it)
- Consistent JSON error responses (`400` / `401` / `403` / `404` / `409`)
- Versioned database schema via Flyway (`V1`–`V4`)

## Getting started

### 1. Database (Docker)

```bash
docker run -d --name taskflow-db \
  -e POSTGRES_PASSWORD=mysecretpassword \
  -p 5432:5432 \
  postgres:16
```

Flyway creates the schema automatically on application startup. To start over with a clean database:

```bash
docker rm -f taskflow-db && docker run -d --name taskflow-db \
  -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 postgres:16
```

### 2. Run the application

```bash
# plain run
./gradlew bootRun

# with the first admin account seeded on startup
ADMIN_EMAIL=admin@taskflow.cz ADMIN_PASSWORD=adminpass123 ./gradlew bootRun
```

The API is served at `http://localhost:8080`.

### 3. Authenticate

```bash
# register (returns access + refresh tokens)
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com","name":"Alice","password":"password123"}'

# use the access token
curl http://localhost:8080/api/projects \
  -H "Authorization: Bearer <accessToken>"

# when the access token expires (15 min), exchange the refresh token for a new pair
curl -X POST http://localhost:8080/api/auth/refresh \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"<refreshToken>"}'
```

Refresh tokens are **single-use**: every call to `/api/auth/refresh` invalidates the presented token and returns a new one.

## Configuration

All values live in `application.properties` with environment-variable overrides:

| Environment variable | Default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/postgres` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `postgres` | Database user |
| `DB_PASSWORD` | `mysecretpassword` | Database password |
| `JWT_SECRET` | committed dev key | Base64-encoded 256-bit HMAC key (`openssl rand -base64 32`) |
| `JWT_EXPIRATION_MS` | `900000` (15 min) | Access token lifetime |
| `JWT_REFRESH_EXPIRATION_MS` | `1209600000` (14 days) | Refresh token lifetime |
| `ADMIN_EMAIL` | *(empty = disabled)* | Seeds/promotes an ADMIN account on startup |
| `ADMIN_PASSWORD` | *(empty = disabled)* | Password for the seeded admin |

## API

Base URL: `http://localhost:8080`

### Auth (public)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register; returns tokens + user. Role is always `USER`. |
| `POST` | `/api/auth/login` | Login with email + password; returns tokens + user |
| `POST` | `/api/auth/refresh` | Exchange a refresh token for a new token pair (rotation) |

### Users

| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/users` | `ADMIN` |
| `GET` | `/api/users` | authenticated |
| `GET` | `/api/users/{id}` | authenticated |
| `PUT` | `/api/users/{id}` | self or `ADMIN` |
| `DELETE` | `/api/users/{id}` | `ADMIN` |

### Projects

| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/projects` | authenticated (caller becomes owner) |
| `GET` | `/api/projects` | authenticated |
| `GET` | `/api/projects/{id}` | authenticated |
| `PUT` | `/api/projects/{id}` | project owner or `ADMIN` |
| `DELETE` | `/api/projects/{id}` | project owner or `ADMIN` (fails with `409` while tasks exist) |

### Tasks

| Method | Endpoint | Access |
|---|---|---|
| `POST` | `/api/projects/{projectId}/tasks` | project owner or `ADMIN` |
| `GET` | `/api/projects/{projectId}/tasks` | authenticated |
| `GET` | `/api/tasks/{id}` | authenticated |
| `PUT` | `/api/tasks/{id}` | project owner, task assignee, or `ADMIN` |
| `DELETE` | `/api/tasks/{id}` | project owner or `ADMIN` |

### Request examples

```jsonc
// POST /api/projects
{ "name": "project1", "description": "My first project" }
// ownership is taken from the token — there is no ownerId field

// POST /api/projects/1/tasks
{
  "title": "Set up CI pipeline",
  "description": "GitHub Actions build + test",
  "priority": "HIGH",          // LOW | MEDIUM | HIGH
  "assigneeId": 2,             // optional
  "dueDate": "2026-07-20"      // ISO-8601
}

// PUT /api/tasks/1  (full replacement)
{
  "title": "Set up CI pipeline",
  "description": "GitHub Actions build + test",
  "status": "IN_PROGRESS",     // TODO | IN_PROGRESS | DONE | CANCELLED
  "priority": "HIGH",
  "assigneeId": null,          // null = unassign
  "dueDate": "2026-07-20"
}
```

### Error format

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Only the project owner can perform this action.",
  "fieldErrors": null,
  "timestamp": "2026-07-13T14:20:32.485"
}
```

Validation failures (`400`) additionally populate `fieldErrors` with a per-field message map.

## Database schema

Managed by Flyway (`src/main/resources/db/migration`), with Hibernate in `validate` mode

| Migration | Change |
|---|---|
| `V1__init_schema.sql` | `users`, `projects`, `tasks` tables + FK indexes |
| `V2__add_user_password.sql` | BCrypt password column |
| `V3__add_user_role.sql` | `USER`/`ADMIN` role column |
| `V4__create_refresh_tokens.sql` | Server-side refresh token store (hashed) |

Relationships: `User 1—N Project` (owner), `Project 1—N Task`, `User 1—N Task` (assignee, optional).

## Security model

Every request passes through a JWT filter that validates the access token's signature and expiry, loads the user, and populates the security context — no server-side sessions. Authorization is two-layered: role checks (`@PreAuthorize`) gate admin operations, while ownership checks in the service layer ensure users can only modify their own projects and tasks (defense against BOLA/IDOR). Passwords are BCrypt-hashed; refresh tokens are random 256-bit values stored only as SHA-256 hashes and rotated on every use, so they can be revoked server-side and a leaked database does not expose usable tokens.
