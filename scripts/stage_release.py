import argparse
import fnmatch
import hashlib
import io
import json
import re
import os
import shutil
import stat
import sys
import urllib.parse
import zipfile
from pathlib import Path

SKIP = {"packwiz.exe", "PW-GUI.exe"}
HOSTED_DIR = "_files"
METAFILES_ZIP = "unsup-metafiles.zip.br"
ZIP_DATE = (2000, 1, 1, 0, 0, 0)


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


def host_client_only(out: Path, rel: str, base_url: str) -> None:
    source = out / rel
    hosted = out / HOSTED_DIR / rel
    hosted.parent.mkdir(parents=True, exist_ok=True)
    data = source.read_bytes()
    source.replace(hosted)
    url = base_url.rstrip("/") + "/" + urllib.parse.quote(f"{HOSTED_DIR}/{rel}")
    name = source.name
    metafile = "\n".join([
        f'name = "{name}"',
        f'filename = "{name}"',
        'side = "client"',
        "",
        "[download]",
        f'url = "{url}"',
        'hash-format = "sha256"',
        f'hash = "{hashlib.sha256(data).hexdigest()}"',
        "",
    ])
    (source.parent / f"{name}.pw.toml").write_text(metafile, encoding="utf-8")


def write_metafiles_zip(out: Path) -> None:
    import brotli
    buf = io.BytesIO()
    with zipfile.ZipFile(buf, "w", zipfile.ZIP_STORED) as zf:
        for path in sorted(out.rglob("*.pw.toml")):
            rel = path.relative_to(out).as_posix()
            if rel.startswith(HOSTED_DIR + "/"):
                continue
            zf.writestr(zipfile.ZipInfo(rel, ZIP_DATE), path.read_bytes())
    (out / METAFILES_ZIP).write_bytes(brotli.compress(buf.getvalue()))
    unsup = out / "unsup.toml"
    text = unsup.read_text(encoding="utf-8").rstrip("\n") if unsup.exists() else ""
    if "metafiles_zip" not in text:
        text = (text + "\n\n" if text else "") + "[features]\nmetafiles_zip = true"
    unsup.write_text(text + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("pack")
    parser.add_argument("out")
    parser.add_argument("--release", required=True)
    parser.add_argument("--version")
    parser.add_argument("--base-url")
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

    if args.base_url:
        client_only = release.get("client_only", [])
        for path in sorted(p for p in out.rglob("*") if p.is_file()):
            rel = path.relative_to(out).as_posix()
            if rel.endswith(".pw.toml") or rel.startswith(HOSTED_DIR + "/"):
                continue
            if any(fnmatch.fnmatchcase(rel, g) for g in client_only):
                host_client_only(out, rel, args.base_url)
        ignore = out / ".packwizignore"
        ignore_text = ignore.read_text(encoding="utf-8").rstrip("\n")
        ignore.write_text(f"{ignore_text}\n{HOSTED_DIR}/\n{METAFILES_ZIP}\n", encoding="utf-8")
        write_metafiles_zip(out)

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
