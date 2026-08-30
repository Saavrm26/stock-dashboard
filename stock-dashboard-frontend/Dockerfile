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

FROM node:24-bookworm-slim AS build

WORKDIR /app

COPY --from=protoc /opt/protoc/bin/protoc /usr/local/bin/protoc
COPY --from=protoc /opt/protoc/include /usr/local/include

RUN corepack enable

COPY package.json yarn.lock .yarnrc.yml ./
RUN --mount=type=cache,target=/root/.cache/node/corepack \
    --mount=type=cache,target=/root/.yarn \
    yarn install --immutable

COPY . .
COPY --from=common / /common

ARG NEXT_PUBLIC_API_BASE_URL_PROD
ENV NEXT_PUBLIC_API_BASE_URL_PROD=$NEXT_PUBLIC_API_BASE_URL_PROD

RUN yarn build

FROM node:24-bookworm-slim AS runtime

WORKDIR /app

ENV NODE_ENV=production
ENV PORT=3000
ENV HOSTNAME=0.0.0.0

RUN groupadd --system app && useradd --system --gid app app

COPY --from=build --chown=app:app /app/.next/standalone ./
COPY --from=build --chown=app:app /app/.next/static ./.next/static

USER app

EXPOSE 3000

ENTRYPOINT ["node", "server.js"]
