#!/bin/bash
set -euxo pipefail

export DEBIAN_FRONTEND=noninteractive

REPO_URL="https://github.com/Saavrm26/stock-dashboard.git"
REPO_DIR="${HOME}/stock-dashboard"

apt-get update
apt-get upgrade -y
apt-get install -y git

if [ ! -d "${REPO_DIR}/.git" ]; then
  git clone "${REPO_URL}" "${REPO_DIR}"
fi
