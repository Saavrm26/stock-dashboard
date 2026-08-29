from __future__ import annotations

import shutil
import subprocess
import sys
from pathlib import Path


def main() -> int:
    project_root = Path(__file__).resolve().parent
    proto_dir = (project_root / ".." / "common" / "proto" / "v1").resolve()
    output_dir = project_root / "app" / "models" / "generated"

    if shutil.which("protoc") is None:
        print("protoc is not available on PATH", file=sys.stderr)
        return 1

    proto_files = sorted(proto_dir.glob("*.proto"))
    if not proto_files:
        print(f"No proto files found in {proto_dir}", file=sys.stderr)
        return 1

    output_dir.mkdir(parents=True, exist_ok=True)

    command = [
        "protoc",
        f"--proto_path={proto_dir}",
        f"--python_out={output_dir}",
        f"--pyi_out={output_dir}",
        *[str(proto_file) for proto_file in proto_files],
    ]
    subprocess.run(command, check=True)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
