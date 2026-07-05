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

## Gotchas

- Tests need Docker (TestContainers pulls `postgres:17`).
- Tests pass with `@ServiceConnection` + Spring Boot test slice; no external DB needed.
- Terraform in `terraform/` provisions VPC + EKS + Aurora DB + k8s resources.
- Kustomize deployment in `deployment/` — three layers: `base/`, `dev/`, `prod/`.
- `.env` files are gitignored; use env vars or docker-compose for local DB/Redis.
