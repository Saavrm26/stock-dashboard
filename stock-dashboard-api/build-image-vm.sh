#!/usr/bin/env bash
set -euo pipefail

IMAGE_TAG="${IMAGE_TAG:-ghcr.io/saavrm26/stock-dashboard/api:latest}"
FORCE_PULL="${FORCE_PULL:-0}"
INSTALL_DOCKER=0
PLATFORM="${PLATFORM:-}"

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "${SCRIPT_DIR}/.." && pwd)"
SERVICE_DIR="${SCRIPT_DIR}"
COMMON_DIR="${COMMON_DIR:-${REPO_ROOT}/common}"
DOCKERFILE="${DOCKERFILE:-${SERVICE_DIR}/Dockerfile}"
DOCKER=()
TARGET_USER="${SUDO_USER:-${USER:-}}"

log() {
  printf '[stock-dashboard-api] %s\n' "$*"
}

die() {
  printf '[stock-dashboard-api] Error: %s\n' "$*" >&2
  exit 1
}

usage() {
  cat <<'EOF'
Usage: ./build-image-vm.sh [--install-docker] [--force-pull] [--help]

Builds the stock-dashboard API image on a Debian/Linux VM.

Environment overrides:
  IMAGE_TAG      Target image tag (default: ghcr.io/saavrm26/stock-dashboard/api:latest)
  COMMON_DIR     Path to the shared common build context (default: ../common)
  DOCKERFILE     Dockerfile path (default: ./Dockerfile)
  PLATFORM       Optional target platform, e.g. linux/amd64
  FORCE_PULL     Set to 1 to refresh base images before building

Examples:
  ./build-image-vm.sh
  FORCE_PULL=1 ./build-image-vm.sh
  IMAGE_TAG=ghcr.io/saavrm26/stock-dashboard/api:dev ./build-image-vm.sh --install-docker
EOF
}

is_debian_like() {
  [[ -f /etc/os-release ]] || return 1
  # shellcheck disable=SC1091
  . /etc/os-release
  [[ "${ID:-}" == "debian" ]] || [[ " ${ID_LIKE:-} " == *" debian "* ]]
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || die "Missing required command: $1"
}

configure_docker_group() {
  [[ -n "${TARGET_USER}" ]] || return 0
  command -v sudo >/dev/null 2>&1 || return 0
  getent group docker >/dev/null 2>&1 || sudo groupadd docker
  sudo usermod -aG docker "${TARGET_USER}"
  log "Added ${TARGET_USER} to the docker group. Future shells can use docker without sudo after 'newgrp docker' or a new login."
}

pick_docker_cmd() {
  if docker info >/dev/null 2>&1; then
    DOCKER=(docker)
    return
  fi

  if command -v sudo >/dev/null 2>&1; then
    if sudo docker info >/dev/null 2>&1; then
      configure_docker_group
      DOCKER=(sudo docker)
      return
    fi
  fi

  die "Docker daemon is not accessible. Install Docker or rerun with --install-docker."
}

install_docker_on_debian() {
  is_debian_like || die "--install-docker is only supported on Debian-like systems."

  if command -v docker >/dev/null 2>&1; then
    log "Docker is already installed."
    return
  fi

  require_cmd curl

  local sudo_cmd=()
  if [[ "${EUID}" -ne 0 ]]; then
    command -v sudo >/dev/null 2>&1 || die "sudo is required to install Docker."
    sudo_cmd=(sudo)
  fi

  log "Installing Docker Engine."
  "${sudo_cmd[@]}" apt-get update
  "${sudo_cmd[@]}" apt-get install -y ca-certificates curl gnupg
  "${sudo_cmd[@]}" install -m 0755 -d /etc/apt/keyrings

  if [[ ! -f /etc/apt/keyrings/docker.asc ]]; then
    curl -fsSL https://download.docker.com/linux/debian/gpg | "${sudo_cmd[@]}" gpg --dearmor -o /etc/apt/keyrings/docker.asc
    "${sudo_cmd[@]}" chmod a+r /etc/apt/keyrings/docker.asc
  fi

  if [[ ! -f /etc/apt/sources.list.d/docker.list ]]; then
    local arch
    arch="$(dpkg --print-architecture)"
    # shellcheck disable=SC1091
    . /etc/os-release
    printf 'deb [arch=%s signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian %s stable\n' \
      "${arch}" "${VERSION_CODENAME}" | "${sudo_cmd[@]}" tee /etc/apt/sources.list.d/docker.list >/dev/null
  fi

  "${sudo_cmd[@]}" apt-get update
  "${sudo_cmd[@]}" apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
  "${sudo_cmd[@]}" systemctl enable --now docker

  if [[ -n "${TARGET_USER}" ]]; then
    getent group docker >/dev/null 2>&1 || "${sudo_cmd[@]}" groupadd docker
    "${sudo_cmd[@]}" usermod -aG docker "${TARGET_USER}"
    log "Added ${TARGET_USER} to the docker group. Start a new shell or run 'newgrp docker' to use docker without sudo."
  fi
}

ensure_base_image() {
  local image="$1"

  if [[ "${FORCE_PULL}" == "1" ]]; then
    log "Refreshing base image ${image}."
    "${DOCKER[@]}" pull "${image}"
    return
  fi

  if "${DOCKER[@]}" image inspect "${image}" >/dev/null 2>&1; then
    log "Base image ${image} already present."
  else
    log "Pulling base image ${image}."
    "${DOCKER[@]}" pull "${image}"
  fi
}

validate_layout() {
  [[ -d "${SERVICE_DIR}" ]] || die "Service directory not found: ${SERVICE_DIR}"
  [[ -d "${COMMON_DIR}" ]] || die "Common build context not found: ${COMMON_DIR}"
  [[ -f "${DOCKERFILE}" ]] || die "Dockerfile not found: ${DOCKERFILE}"
}

build_image() {
  local args=()
  args=(build --build-context "common=${COMMON_DIR}" -t "${IMAGE_TAG}" -f "${DOCKERFILE}")

  if [[ -n "${PLATFORM}" ]]; then
    args+=(--platform "${PLATFORM}")
  fi

  args+=("${SERVICE_DIR}")

  log "Building ${IMAGE_TAG}."
  DOCKER_BUILDKIT=1 "${DOCKER[@]}" "${args[@]}"
  log "Build complete: ${IMAGE_TAG}"
}

main() {
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --install-docker)
        INSTALL_DOCKER=1
        ;;
      --force-pull)
        FORCE_PULL=1
        ;;
      --help|-h)
        usage
        exit 0
        ;;
      *)
        die "Unknown argument: $1"
        ;;
    esac
    shift
  done

  validate_layout

  if [[ "${INSTALL_DOCKER}" == "1" ]]; then
    install_docker_on_debian
  fi

  require_cmd docker
  pick_docker_cmd
  ensure_base_image "eclipse-temurin:21-jdk"
  build_image
}

main "$@"
