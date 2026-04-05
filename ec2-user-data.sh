#!/bin/bash
set -euxo pipefail

mkdir /app
chown admin:admin /app
export DEBIAN_FRONTEND=noninteractive

REPO_URL="https://github.com/Saavrm26/stock-dashboard.git"
REPO_DIR="/app/stock-dashboard"

apt-get update -y
apt-get upgrade -y
apt-get install -y git

if [ ! -d "${REPO_DIR}/.git" ]; then
  sudo -u admin git clone "${REPO_URL}" "${REPO_DIR}"
fi
