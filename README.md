# Workout App

A workout scheduler built as Spring Boot microservices. Users plan workouts, pick exercises from a shared catalogue, and track their schedule. Exercises submitted by users go through an admin review before they become available to everyone.

## Architecture

```
                       ┌──────────────────────────┐
  REST clients ──────► │ api-gateway        :8080 │ ──┐
                       └──────────────────────────┘   │  lb://<service> (resolved through Eureka)
                                                      ▼
  ┌──────────────────────────┐   ┌──────────────────────────┐   ┌──────────────────────────┐
  │ exercise-service   :8082 │   │ user-service       :8083 │   │ schedule-service   :8084 │
  │ exercises-db             │   │ workout-scheduler-users-db│  │ schedules-db             │
  └──────────────────────────┘   └──────────────────────────┘   └──────────────────────────┘
               ▲
               │ @LoadBalanced RestClient (http://ExercisesService)
  ┌──────────────────────────┐
  │ workout-service    :8081 │ ◄── browser (Thymeleaf web app)
  │ workout-scheduler-db     │
  └──────────────────────────┘

  Every service registers with eureka-discovery-service :8761
```

| Module | Port | What it does | Database |
|---|---|---|---|
| `eureka-discovery-service` | 8761 | Service registry (Netflix Eureka server) | – |
| `api-gateway` | 8080 | Single entry point for the REST APIs (Spring Cloud Gateway, WebFlux) | – |
| `workout-service` | 8081 | Web app: login, exercises, workouts, schedules | `workout-scheduler-db` |
| `exercise-service` | 8082 | Exercise catalogue, images, review/approval | `exercises-db` |
| `user-service` | 8083 | User accounts and roles | `workout-scheduler-users-db` |
| `schedule-service` | 8084 | Daily schedules | `schedules-db` |

Each service owns its database. workout-service reads exercises from exercise-service through a load-balanced `RestClient`; it does not call user-service or schedule-service (it still keeps its own users and schedules).

## Tech stack

- Java 21
- Spring Boot 3.5.11, Spring Data JPA, Spring Security, Spring HATEOAS, Thymeleaf
- Spring Cloud 2025.0.3: Netflix Eureka, Spring Cloud Gateway (WebFlux), Spring Cloud LoadBalancer
- MySQL 8 (runtime), H2 (tests)
- Maven multi-module build with the Maven wrapper

## Prerequisites

- JDK 21
- MySQL 8 on `localhost:3306` — the services create their databases on first start (`createDatabaseIfNotExist=true`, `ddl-auto=update`)

## Configuration

exercise-service, user-service, schedule-service and api-gateway read their settings from environment variables. Each of these modules has a `secrets.env` file for local development; `*.env` files are git-ignored, so create them yourself:

```properties
# exercise-service/secrets.env  (user-service: 8083 / workout-scheduler-users-db, schedule-service: 8084 / schedules-db)
DB_URL=jdbc:mysql://localhost:3306/exercises-db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=<mysql user>
DB_PASSWORD=<mysql password>
PORT=8082
```

user-service and api-gateway also need the JWT signing secret, and it must be **the same value in both** — user-service signs the login token with it and the gateway verifies it:

```properties
# user-service/secrets.env (in addition to the DB_* and PORT lines above)
TOKEN_SECRET=<random secret, at least 64 bytes>

# api-gateway/secrets.env
TOKEN_SECRET=<the same secret>
```

Generate one with `openssl rand -base64 64`. user-service refuses to start with a secret shorter than 64 bytes (HS512 needs a 512-bit key).

| Variable | Used by | Default |
|---|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | exercise-, user-, schedule-service | – (required) |
| `TOKEN_SECRET` | user-service, api-gateway | – (required, ≥ 64 bytes, same in both) |
| `TOKEN_EXPIRATION_TIME` | user-service | `3600` (seconds the login token and cookie last) |
| `TOKEN_COOKIE_SECURE` | user-service | `false` — set `true` behind HTTPS so the cookie is only sent over TLS |
| `PORT` | exercise-, user-, schedule-service; api-gateway; eureka | api-gateway `8080`, eureka `8761` |
| `EUREKA_URL` | all Eureka clients | `http://localhost:8761/eureka` |

workout-service keeps its database connection and port (8081) in `workout-service/src/main/resources/application.properties`.

## Build and test

```bash
./mvnw package          # build every module (runs the tests)
./mvnw test             # tests only
./mvnw -pl exercise-service test
```

Tests run against in-memory H2 with Eureka disabled, so they need neither MySQL nor a running registry.

## Running locally

Start the registry first, then the services, then the gateway:

```bash
./mvnw package -DskipTests

java -jar eureka-discovery-service/target/eureka-discovery-service-0.0.1-SNAPSHOT.jar

# each of these in its own terminal, from the repository root
(cd exercise-service && env $(grep -v '^#' secrets.env | xargs) java -jar target/exercise-service-0.0.1-SNAPSHOT.jar)
(cd user-service     && env $(grep -v '^#' secrets.env | xargs) java -jar target/user-service-0.0.1-SNAPSHOT.jar)
(cd schedule-service && env $(grep -v '^#' secrets.env | xargs) java -jar target/schedule-service-0.0.1-SNAPSHOT.jar)
java -jar workout-service/target/workout-service-0.0.1-SNAPSHOT.jar
(cd api-gateway      && env $(grep -v '^#' secrets.env | xargs) java -jar target/api-gateway-0.0.1-SNAPSHOT.jar)
```

Don't `source` the `secrets.env` files in a shell: the `&` characters in `DB_URL` break it. The `env $(...)` form above passes them through unchanged. In IntelliJ, a run configuration with the variables (or an EnvFile plugin) works too.

Then open:

- Eureka dashboard: http://localhost:8761
- Web app: http://localhost:8081 — on an empty database workout-service creates an admin account `user` / `password` at startup (change it); accounts you register get the USER role
- APIs through the gateway: http://localhost:8080/api/...

## API gateway

| Route | Path | Routed to | Login required |
|---|---|---|---|
| `exercise-service` | `/api/exercises/**` | `lb://ExercisesService` | yes |
| `user-service-public` | `/api/users/login`, `/create`, `/create-password-reset-token`, `/change-password` | `lb://UsersService` | no |
| `user-service` | every other `/api/users/**` | `lb://UsersService` | yes |
| `schedule-service` | `/api/schedules/**` | `lb://ScheduleService` | yes |

Routes are configured in `api-gateway/src/main/resources/application.properties` under `spring.cloud.gateway.server.webflux.routes`. The first matching route wins, so `user-service-public` is listed before `user-service`.

### Authentication

1. `POST /api/users/login` with `{"username": "...", "password": "..."}`. user-service checks the BCrypt password hash and answers with the user and a `JWT` cookie: `HttpOnly`, `SameSite=Lax`, `Path=/`, lasting `TOKEN_EXPIRATION_TIME`. The token is signed with HS512 and carries the user id (`sub`), `username` and `roles`.
2. The browser (or `curl -c/-b`) sends the cookie back on later requests.
3. On routes marked "Login required", the gateway's `JwtAuthentication` filter verifies the cookie's signature and expiry with `TOKEN_SECRET`. A missing, tampered or expired token gets `401` from the gateway and never reaches the service.

```bash
curl -c cookies.txt -H 'Content-Type: application/json' \
     -d '{"username":"someone","password":"S3cret@pass"}' http://localhost:8080/api/users/login
curl -b cookies.txt http://localhost:8080/api/exercises      # 200 — without -b: 401
```

A wrong username or password both return `401` with the same message, so the API doesn't reveal which usernames exist.

The check lives only in the gateway: calling a service directly on its own port skips it, so the service ports shouldn't be reachable from outside.

HATEOAS links returned through the gateway point at the gateway, not at the service. That needs two settings working together:

- the services set `server.forward-headers-strategy=framework`, so they build links from the `X-Forwarded-*` headers;
- the gateway sets `spring.cloud.gateway.server.webflux.trusted-proxies` (localhost by default). Without it, this gateway version strips `X-Forwarded-*` headers instead of adding them.

## REST APIs

All responses are HAL JSON (`application/hal+json`).

**exercise-service** — `/api/exercises`

| Method | Path | Description |
|---|---|---|
| GET | `/` | Approved exercises; `?target=CHEST&target=LEGS` filters by body part |
| GET | `/search` | Paged search: `name`, `target`, `complexity`, `movement`, `page`, `size`, `orderBy` |
| GET | `/{id}` | One exercise |
| POST | `/add` | Submit an exercise; it waits for review |
| PATCH | `/{id}` | Edit name, description, body part, complexity, movement |
| DELETE | `/{id}` | Soft-delete |
| GET | `/for-review`, `/for-review/count` | Exercises waiting for review |
| PATCH | `/{id}/approve` | Approve an exercise |
| GET, POST | `/{id}/images` | List or add image URLs |
| GET, DELETE | `/{id}/images/{imageId}` | One image |

**user-service** — `/api/users`

| Method | Path | Description |
|---|---|---|
| POST | `/create` | Create a user |
| POST | `/login` | Log in; sets the `JWT` cookie (see [Authentication](#authentication)) |
| GET | `/{id}`, `/{id}/roles` | User details and roles |
| PATCH | `/{id}` | Update profile |
| POST | `/create-password-reset-token` | Start a password reset (`?email=`) |
| PATCH | `/change-password` | Change password with a reset token |

**schedule-service** — `/api/schedules`

| Method | Path | Description |
|---|---|---|
| POST | `/add` | Create a schedule |
| GET | `/{userId}` | All schedules of a user |
| GET | `/{userId}?date=YYYY-MM-DD` | The schedule for one day |
| PATCH | `/{userId}` | Update a schedule |

## Web app (workout-service)

- **Exercises** — browse by muscle group, view, and submit new exercises. Admins see a review queue where they approve, edit or delete submissions.
- **Workouts** — create a workout (type, target body parts, date) and add exercises with sets, reps, rest and weight; the exercise list offers approved exercises for the workout's target body parts.
- **Schedules** — daily and weekly views.

## Known gaps

- The Programs page (`/programs`) is not implemented yet and returns 404.
- schedule-service expects at most one schedule per user per day; `GET /api/schedules/{userId}?date=` returns 500 when there are several.
- The workout form requires a weight of at least 0.5, so bodyweight exercises can't be logged with zero weight.
- The dashboard, profile progress bars and weekly schedule show placeholder content.
- Only the gateway checks the JWT; the services themselves accept requests without one, so anything that can reach their ports directly bypasses the login.
- On an empty user database `POST /api/users/create` returns 500: the USER role isn't created automatically. Insert the `USER` and `ADMIN` rows into `roles` first.
