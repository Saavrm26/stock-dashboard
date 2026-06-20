# CLAUDE.md — stock-dashboard-api

**Stack:** Kotlin 2.2.21 + Spring Boot 3.3.5 / Java 21 / Gradle

## Commands

```sh
gradle build       # compile + test + bootJar (use `gradle`, not `./gradlew`)
gradle test        # JUnit 5 with TestContainers (PostgreSQL 17)
gradle bootRun     # requires DB_URL + DB_USER env vars; profile=local by default
```

## Key facts

- **Entrypoint:** `src/main/kotlin/xyz/saarthakdevelopsstuff/stock_dashboard_api/StockDashboardApiApplication.kt`
- **Package root:** `xyz.saarthakdevelopsstuff.stock_dashboard_api`
- **Profiles:** `local` (dev overrides from `application-local.yaml`), `prd` (AWS prod). Default is empty profile → `application.yaml`.
- **Auth:** Cognito OAuth2/OIDC via Spring Security. Two separate pools for dev/prd. Secrets come from AWS Parameter Store (`/stock-dashboard/{profile}/cognito`).
- **DB:** PostgreSQL + Flyway migrations (`src/main/resources/db/migration/`), `ddl-auto: validate`.
- **Test profile** (`application-test.yml`) can use H2 in-memory; `@TestContainers` config uses PostgreSQL 17 via `TestContainersConfiguration.kt`.
- **Virtual threads** enabled (`spring.threads.virtual.enabled: true`).
- **MapStruct** with KAPT for entity/DTO mapping.
- **Protobuf** from `../common/proto/v1/` — protos are compiled at build time by `com.google.protobuf` Gradle plugin. The API communicates with the sidecar via protobuf HTTP (`ProtobufHttpMessageConverter`).

## Local development

```sh
docker compose up                    # starts Postgres (5432) + Redis (6379)
DB_URL=localhost:5432/stock_dashboard DB_USER=postgres gradle bootRun
```

- Cognito client-secret is fetched from AWS Parameter Store at runtime. If unavailable locally, test with `--spring.profiles.active=test` or mock the OAuth2 flow.
- The sidecar (Python) runs on `localhost:8081` — start it separately if you need stock data.

## Layer conventions

- **Controllers:** Own HTTP concerns only: routing, request validation, authentication context, DTO conversion, and response handling. Delegate business behavior to use-case services. Do not access databases, caches, or third-party clients directly.
- **Use-case services:** Services such as `StockServiceV1` and `WatchListServiceV1` own business outcomes and orchestration. They compose `TxService`, `CacheService`, and third-party clients without containing HTTP or infrastructure-specific logic.
- **Application services:** `TxService` owns database reads and writes. `CacheService` owns cache reads and writes. Both must read and return service-layer models rather than exposing database entities, cache models, or infrastructure types.
- **Database:** Database repositories, entities, queries, and persistence mappings are implementation details behind `TxService`. Database concerns must not leak into controllers or use-case services.
- **Cache:** Cache keys, serialization, TTLs, and invalidation are implementation details behind `CacheService`. Cache behavior must not contain business decisions.
- **Third-party clients:** Clients are HTTP adapters for external services. They own external request/response formats and translate them into service-layer models. External client details must not leak into controllers or database/cache services.

## Gotchas

- Tests need Docker (TestContainers pulls `postgres:17`).
- Tests pass with `@ServiceConnection` + Spring Boot test slice; no external DB needed.
- Terraform in `terraform/` provisions VPC + EKS + Aurora DB + k8s resources.
- Kustomize deployment in `deployment/` — three layers: `base/`, `dev/`, `prod/`.
- `.env` files are gitignored; use env vars or docker-compose for local DB/Redis.
