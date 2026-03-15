# syntax=docker/dockerfile:1.7

FROM debian:bookworm-slim AS protoc

ARG PROTOC_VERSION=30.2
ARG TARGETOS=linux
ARG TARGETARCH

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl unzip \
    && rm -rf /var/lib/apt/lists/*

RUN case "${TARGETARCH}" in \
        amd64) PROTOC_ARCH=x86_64 ;; \
        arm64) PROTOC_ARCH=aarch_64 ;; \
        *) echo "Unsupported architecture: ${TARGETARCH}" >&2; exit 1 ;; \
    esac \
    && curl -fsSL -o /tmp/protoc.zip "https://github.com/protocolbuffers/protobuf/releases/download/v${PROTOC_VERSION}/protoc-${PROTOC_VERSION}-${TARGETOS}-${PROTOC_ARCH}.zip" \
    && unzip /tmp/protoc.zip -d /opt/protoc \
    && rm /tmp/protoc.zip

FROM python:3.13-slim AS build

LABEL authors="Saavrm26"

COPY --from=docker.io/astral/uv:0.10.9 /uv /uvx /bin/
COPY --from=protoc /opt/protoc/bin/protoc /usr/local/bin/protoc
COPY --from=protoc /opt/protoc/include /usr/local/include

WORKDIR /app

# Fast startup
# Copy packages from global to venv
ENV UV_COMPILE_BYTECODE=1 \
    UV_LINK_MODE=copy \
    UV_PYTHON_DOWNLOADS=never

COPY pyproject.toml uv.lock ./

# 2. Cache + different layers of (deps + project build) improves build time
RUN --mount=type=cache,target=/root/.cache/uv \
    uv sync --frozen --no-install-project --no-dev

COPY . .
COPY --from=common / /common

RUN --mount=type=cache,target=/root/.cache/uv \
    mkdir -p app/models/generated \
    && uv run compile_protos.py \
    && uv sync --frozen --no-dev

FROM python:3.13-slim AS runtime

# Non-root user for security
RUN groupadd -r app && useradd -r -d /app -g app -N app

WORKDIR /app

# Copy built app + venv from build stage
COPY --from=build --chown=app:app /app /app

# Activate the venv
ENV PATH="/app/.venv/bin:$PATH"

USER app

ENTRYPOINT ["python", "-m", "main"]