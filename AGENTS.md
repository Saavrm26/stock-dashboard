# AGENTS.md

## Architecture

Three services sharing protobuf schemas in `common/proto/v1/`:

| Directory | Stack | Entrypoint | Port |
|-----------|-------|------------|------|
| `stock-dashboard-api/` | Kotlin + Spring Boot 3.3.5 / Java 21 / Gradle | `StockDashboardApiApplication.kt` | 8080 |
| `stock-dashboard-api-sidecar/` | Python 3.13 + FastAPI / uv | `main.py` (uvicorn) | 8081 |
| `stock-dashboard-frontend/` | Next.js 16 + React 19 + Tailwind v4 / Yarn | `app/layout.tsx` | 3000 |

**Flow:** Frontend → API (authn/z, BFF) → Sidecar (Yahoo Finance data, gRPC-style protobuf HTTP via `ProtobufHttpMessageConverter`).

## Developer commands

### API (`stock-dashboard-api/`)
```sh
./gradlew build          # full build (compile + test + bootJar)
./gradlew test           # JUnit 5 with TestContainers (PostgreSQL 17)
./gradlew bootRun        # run locally (requires DB_URL/DB_USER env)
```
Profiles: `local` (dev overrides), `prd` (production). Default profile loads `application.yaml`.

### Sidecar (`stock-dashboard-api-sidecar/`)
```sh
uv sync                  # install deps
uv run python -m main    # start on 127.0.0.1:8081 with hot-reload
uv run compile_protos.py # regenerate protobuf stubs into app/models/generated/
```

### Frontend (`stock-dashboard-frontend/`)
```sh
yarn install --immutable  # install deps (amplify CI uses yarn 4.9.2 via corepack)
yarn dev                  # next dev on :3000
yarn build                # next build
yarn lint                 # eslint (next/core-web-vitals)
yarn proto:gen            # regenerate TS protobuf stubs to model/generated/
```
`prebuild` hook runs `proto:gen` automatically.

### Infra
```sh
# Terraform (cd stock-dashboard-api/terraform)
terraform apply  # provisions VPC, EKS, Aurora DB, k8s resources

# Kustomize deploy (cd stock-dashboard-api/deployment)
kubectl apply -k dev   # local dev overlay
kubectl apply -k prod  # production overlay
```

## Key conventions

- **Protobuf generated code** is checked into each service's `generated/` dir (or gitkeep'd). Run the service-specific codegen script after changing `.proto` files.
- **Flyway migrations** in `stock-dashboard-api/src/main/resources/db/migration/` with `ddl-auto: validate`.
- **TestContainers** used for integration tests (PostgreSQL 17 container via `@ServiceConnection`). Test profile can also use H2 (`application-test.yml`).
- **Cognito OAuth2** for auth. Two Cognito pools (dev/prd). Secrets from AWS Secrets Manager / Parameter Store via Spring Cloud AWS.
- **Config per profile:** Base `application.yaml` + `application-{local,prd}.yaml`. Test overrides in `src/test/resources/`.
- **Two CI workflows** (`.github/workflows/`): `api-docker.yml` and `sidecar-docker.yml`. Both build on push to `main` when their paths change.
- **Docker builds** need `--build-context common=../common` for protos (both API and sidecar).
- **Kustomize** three-tier: `base/` (Deployment + Service), `dev/` overlay, `prod/` overlay (adds ingress, SA, Aurora CA certs).
- **Virtual threads** enabled (`spring.threads.virtual.enabled: true`).
- **MapStruct** with KAPT for entity/DTO mapping.
- **Frontend** uses `@/` path alias (maps to repo root), `model/generated/` excluded from TS compilation.

## Gotchas

- API tests require Docker (for TestContainers PostgreSQL image).
- `DB_URL` and `DB_USER` env vars must be set for local API runs (use `docker compose up` for Postgres+Redis).
- Sidecar uses `uv` (not pip/poetry). Run `uv sync` after pulling.
- Frontend has both `yarn.lock` and `package-lock.json` — CI uses Yarn.
- Terraform `k8s` module applies k8s manifests via `kubectl` and depends on EKS + DB.
