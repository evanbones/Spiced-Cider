import argparse
import hashlib
import json
import os
import re
import shutil
import stat
import subprocess
import sys
import tomllib
import urllib.request
from pathlib import Path

CONTENT_DIRS = ("mods", "resourcepacks", "shaderpacks")
DOWNLOAD_HOST = "https://cdn.modrinth.com/"
DOWNLOAD_MANIFEST = "config/spicedcider/resource_pack_downloads.json"
TEXT_SUFFIXES = (".json", ".json5", ".txt", ".toml", ".cfg", ".properties")
USER_AGENT = "spiced-cider-pack-build"


def make_writable(func, path, _):
    os.chmod(path, stat.S_IWRITE | stat.S_IREAD | stat.S_IEXEC)
    func(path)


def file_hash(path: Path, fmt: str) -> str:
    digest = hashlib.new(fmt)
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1 << 20), b""):
            digest.update(chunk)
    return digest.hexdigest()


def fetch(url: str, fmt: str, expected: str, cache: Path) -> Path:
    cached = cache / (expected if fmt == "sha512" else f"{fmt}-{expected}")
    if cached.is_file():
        return cached
    cache.mkdir(parents=True, exist_ok=True)
    part = cached.with_name(cached.name + ".part")
    request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    with urllib.request.urlopen(request, timeout=120) as response, part.open("wb") as out:
        shutil.copyfileobj(response, out)
    actual = file_hash(part, fmt)
    if actual != expected:
        part.unlink()
        raise RuntimeError(f"{url}: {fmt} mismatch, expected {expected}, got {actual}")
    part.replace(cached)
    return cached


def cf_toml(name: str, filename: str, side: str, sha1: str, project_id: int, file_id: int) -> str:
    return (
        f'name = {json.dumps(name, ensure_ascii=False)}\n'
        f'filename = {json.dumps(filename, ensure_ascii=False)}\n'
        f'side = "{side}"\n\n'
        f'[download]\nhash-format = "sha1"\nhash = "{sha1}"\nmode = "metadata:curseforge"\n\n'
        f'[update]\n[update.curseforge]\nfile-id = {file_id}\nproject-id = {project_id}\n'
    )


def escape_json(name: str) -> str:
    return "".join(f"\\u{ord(c):04x}" if c in "&'<>" else c for c in name)


class Entry:
    def __init__(self, path: Path, rel: str, data: dict | None):
        self.path = path
        self.rel = rel
        self.data = data
        self.local: Path | None = None

    @property
    def is_meta(self) -> bool:
        return self.data is not None

    @property
    def modrinth(self) -> dict | None:
        return self.data.get("update", {}).get("modrinth") if self.data else None

    @property
    def key(self) -> str:
        return self.modrinth["mod-id"] if self.modrinth else self.rel

    @property
    def name(self) -> str:
        return self.data["name"] if self.data else self.path.name

    @property
    def side(self) -> str:
        return (self.data.get("side") or "both") if self.data else "both"

    @property
    def target_dir(self) -> Path:
        return self.path.parent

    @property
    def filename(self) -> str:
        return self.data["filename"] if self.data else self.path.name

    @property
    def is_resource_pack(self) -> bool:
        return self.rel.startswith("resourcepacks/")


def collect(out: Path) -> list[Entry]:
    entries = []
    for top in CONTENT_DIRS:
        root = out / top
        if not root.is_dir():
            continue
        for path in sorted(root.iterdir()):
            rel = path.relative_to(out).as_posix()
            if path.name.endswith(".pw.toml"):
                data = tomllib.loads(path.read_text(encoding="utf-8"))
                if data.get("update", {}).get("curseforge"):
                    continue
                entries.append(Entry(path, rel, data))
            elif path.is_file() and path.suffix in (".jar", ".zip"):
                entries.append(Entry(path, rel, None))
    return entries


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("release")
    parser.add_argument("out")
    parser.add_argument("--config", required=True)
    parser.add_argument("--cache", required=True)
    parser.add_argument("--packwiz", required=True)
    args = parser.parse_args()

    release = Path(args.release).resolve()
    out = Path(args.out).resolve()
    cache = Path(args.cache).resolve()
    config = json.loads(Path(args.config).read_text(encoding="utf-8"))
    pins = config.get("pins", {})
    allowed = config.get("overrides", {})

    if out.exists():
        shutil.rmtree(out, onerror=make_writable)
    shutil.copytree(release, out, copy_function=shutil.copyfile)

    entries = collect(out)
    download_keys = set(config.get("download", []))
    forced = [e for e in entries if e.key in download_keys]
    entries = [e for e in entries if e not in forced]
    errors = []
    used_pins = set()
    renames = {}

    for entry in list(entries):
        pin = pins.get(entry.key)
        if not pin:
            continue
        used_pins.add(entry.key)
        current = entry.modrinth["version"] if entry.modrinth else None
        if current != pin["modrinth-version"]:
            errors.append(f"pin for {entry.name} ({entry.key}) is for Modrinth version {pin['modrinth-version']}, "
                          f"pack has {current}: update its CurseForge file in {Path(args.config).name}")
            continue
        entry.path.write_text(cf_toml(pin["name"], pin["filename"], entry.side, pin["sha1"],
                                      pin["project-id"], pin["file-id"]), encoding="utf-8")
        if entry.is_resource_pack and entry.filename != pin["filename"]:
            renames[entry.filename] = pin["filename"]
        entries.remove(entry)

    for entry in forced:
        if not (entry.is_resource_pack and entry.is_meta):
            errors.append(f"{entry.name}: only Modrinth resource packs can be listed under download")
    for key in sorted(download_keys - {e.key for e in forced}):
        errors.append(f"download entry {key} matched nothing in the pack")
    for key in sorted(set(pins) - used_pins):
        errors.append(f"pin {key} ({pins[key]['name']}) matched nothing in the pack")

    detect = out.parent / (out.name + "-detect")
    if detect.exists():
        shutil.rmtree(detect, onerror=make_writable)
    (detect / "mods").mkdir(parents=True)
    shutil.copyfile(out / "pack.toml", detect / "pack.toml")
    (detect / "index.toml").write_text('hash-format = "sha256"\n', encoding="utf-8")

    by_sha1 = {}
    for i, entry in enumerate(entries):
        if entry.is_meta:
            download = entry.data["download"]
            entry.local = fetch(download["url"], download["hash-format"], download["hash"], cache)
        else:
            entry.local = entry.path
        shutil.copyfile(entry.local, detect / "mods" / f"f{i:04d}.jar")
        by_sha1.setdefault(file_hash(entry.local, "sha1"), []).append(entry)

    result = subprocess.run([args.packwiz, "curseforge", "detect"], cwd=detect, capture_output=True, text=True)
    if result.returncode != 0:
        print(result.stdout + result.stderr, file=sys.stderr)
        return 1

    converted = set()
    for meta in sorted(p for p in detect.rglob("*.pw.toml")):
        data = tomllib.loads(meta.read_text(encoding="utf-8"))
        cf = data.get("update", {}).get("curseforge")
        if not cf:
            continue
        for entry in by_sha1.get(data["download"]["hash"], []):
            text = cf_toml(data["name"], data["filename"], entry.side, data["download"]["hash"],
                           cf["project-id"], cf["file-id"])
            if entry.is_resource_pack and entry.filename != data["filename"]:
                renames[entry.filename] = data["filename"]
            if entry.is_meta:
                entry.path.write_text(text, encoding="utf-8")
            else:
                entry.path.unlink()
                stem = re.sub(r"[^a-z0-9]+", "-", entry.path.stem.lower()).strip("-")
                (entry.target_dir / f"{stem}.pw.toml").write_text(text, encoding="utf-8")
            converted.add(id(entry))

    shutil.rmtree(detect, onerror=make_writable)

    remaining = [e for e in entries if id(e) not in converted] + forced
    downloads = [e for e in remaining if e.is_resource_pack and e.is_meta
                 and e.data["download"].get("url", "").startswith(DOWNLOAD_HOST)]
    bundled = [e for e in remaining if e not in downloads]
    warnings = []

    if downloads:
        manifest = {"packs": []}
        for entry in downloads:
            download = entry.data["download"]
            if download["hash-format"] != "sha512":
                errors.append(f"{entry.name}: download needs a sha512 hash, has {download['hash-format']}")
                continue
            manifest["packs"].append({"name": entry.name, "filename": entry.filename,
                                      "url": download["url"], "sha512": download["hash"]})
            entry.path.unlink()
        target = out / DOWNLOAD_MANIFEST
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(json.dumps(manifest, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")

    if renames:
        hits = dict.fromkeys(renames, 0)
        for path in sorted(p for p in (out / "config").rglob("*") if p.is_file() and p.suffix in TEXT_SUFFIXES):
            text = path.read_text(encoding="utf-8", errors="surrogateescape")
            updated = text
            for old, new in renames.items():
                for before, after in ((f"file/{old}", f"file/{new}"),
                                      (f'"{old}"', f'"{new}"'),
                                      (f'"{escape_json(old)}"', f'"{escape_json(new)}"')):
                    count = updated.count(before)
                    if count:
                        hits[old] += count
                        updated = updated.replace(before, after)
            if updated != text:
                path.write_text(updated, encoding="utf-8", errors="surrogateescape")
        for old, new in renames.items():
            print(f"resource pack renamed on CurseForge: {old} -> {new} ({hits[old]} references updated)")
    for entry in bundled:
        status = allowed.get(entry.key, {}).get("status")
        if status is None:
            errors.append(f"{entry.name} ({entry.rel}, key {entry.key}) is not on CurseForge and not listed in "
                          f"{Path(args.config).name} overrides")
        elif status != "approved":
            warnings.append(f"{entry.name}: {status}")
    for key in sorted(set(allowed) - {e.key for e in bundled}):
        warnings.append(f"overrides entry {key} ({allowed[key].get('name')}) is no longer bundled")

    print(f"CurseForge metadata: {len(converted) + len(used_pins)} files "
          f"({len(converted)} by fingerprint, {len(used_pins)} pinned); downloaded by Cider Core: {len(downloads)}; "
          f"bundled in overrides: {len(bundled)}")
    for line in warnings:
        print(f"warning: {line}")
    if errors:
        for line in errors:
            print(f"error: {line}", file=sys.stderr)
        return 1
    print(f"Staged CurseForge pack in {out}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
