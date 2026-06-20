#!/usr/bin/env bash
set -euo pipefail

URL="https://github.com/protocolbuffers/protobuf/releases/download/v35.1/protoc-35.1-linux-x86_64.zip"

curl -fLO "$URL"

unzip -o protoc-35.1-linux-x86_64.zip -d /tmp/protoc-install
mkdir -p "$HOME/.local/bin"
mv /tmp/protoc-install/bin/protoc "$HOME/.local/bin/"
chmod +x "$HOME/.local/bin/protoc"
rm -rf /tmp/protoc-install protoc-35.1-linux-x86_64.zip

echo "protoc installed to $HOME/.local/bin/protoc"
"$HOME/.local/bin/protoc" --version