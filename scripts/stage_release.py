import argparse
import fnmatch
import json
import re
import os
import shutil
import stat
import sys
from pathlib import Path

SKIP = {"packwiz.exe", "PW-GUI.exe"}


def make_writable(func, path, _):
    os.chmod(path, stat.S_IWRITE | stat.S_IREAD | stat.S_IEXEC)
    func(path)


def pw_target(path: Path, rel: str) -> str | None:
    if not rel.endswith(".pw.toml"):
        return None
    match = re.search(r'(?m)^filename\s*=\s*"([^"]+)"', path.read_text(encoding="utf-8"))
    if not match:
        return None
    parent = rel.rsplit("/", 1)[0] if "/" in rel else ""
    return f"{parent}/{match.group(1)}" if parent else match.group(1)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("pack")
    parser.add_argument("out")
    parser.add_argument("--release", required=True)
    parser.add_argument("--version")
    args = parser.parse_args()

    pack = Path(args.pack).resolve()
    out = Path(args.out).resolve()
    release = json.loads(Path(args.release).read_text(encoding="utf-8"))

    if out.exists():
        shutil.rmtree(out, onerror=make_writable)
    shutil.copytree(pack, out, copy_function=shutil.copyfile,
                    ignore=lambda d, names: [n for n in names if n in SKIP or n.endswith(".orig")])
    for root, dirs, _ in os.walk(out):
        for name in dirs:
            os.chmod(os.path.join(root, name), stat.S_IWRITE | stat.S_IREAD | stat.S_IEXEC)

    unmatched = set(release.get("remove", [])) | set(release.get("json", {}))

    for path in sorted(p for p in out.rglob("*") if p.is_file()):
        rel = path.relative_to(out).as_posix()
        candidates = [rel]
        target = pw_target(path, rel)
        if target:
            candidates.append(target)
        hits = [g for g in release.get("remove", []) if any(fnmatch.fnmatchcase(c, g) for c in candidates)]
        if hits:
            unmatched.difference_update(hits)
            path.unlink()

    for rel, patch in release.get("json", {}).items():
        path = out / rel
        if not path.is_file():
            continue
        data = json.loads(path.read_text(encoding="utf-8-sig"))
        data.update(patch)
        path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
        unmatched.discard(rel)

    if args.version:
        toml = out / "pack.toml"
        text = toml.read_text(encoding="utf-8")
        toml.write_text(re.sub(r'(?m)^version = ".*"$', f'version = "{args.version}"', text, count=1), encoding="utf-8")

    if unmatched:
        print(f"pack-release.json entries matched nothing: {sorted(unmatched)}", file=sys.stderr)
        return 1
    print(f"Staged release pack in {out}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
