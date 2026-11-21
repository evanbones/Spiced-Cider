import hashlib
import re
import subprocess
import sys
from pathlib import Path


def sha256(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main():
    pack = Path(sys.argv[1]).resolve()
    repo = Path(subprocess.check_output(["git", "rev-parse", "--show-toplevel"], cwd=pack, text=True).strip())
    rel = pack.relative_to(repo).as_posix()
    subprocess.check_call(["git", "checkout", "--", f"{rel}/index.toml", f"{rel}/pack.toml"], cwd=repo)
    status = subprocess.check_output(["git", "status", "--porcelain", "-z", "--untracked-files=all", "--", rel], cwd=repo, text=True)
    changed = {}
    for entry in filter(None, status.split("\0")):
        path = entry[3:]
        name = path[len(rel) + 1:]
        changed[name] = (pack / name).is_file()

    index_path = pack / "index.toml"
    text = index_path.read_text(encoding="utf-8")
    head, *blocks = text.split("\n[[files]]\n")
    entries = {}
    for block in blocks:
        name = re.search(r'^file = "(.*)"$', block, re.M).group(1)
        entries[name] = block.rstrip("\n")
    for name, exists in changed.items():
        if not exists:
            entries.pop(name, None)
            continue
        lines = [f'file = "{name}"', f'hash = "{sha256(pack / name)}"']
        if name.endswith(".pw.toml"):
            lines.append("metafile = true")
        entries[name] = "\n".join(lines)
        print(f"index: {name}")
    ordered = [entries[name] for name in sorted(entries, key=lambda n: n.encode())]
    index_path.write_text(head.rstrip("\n") + "\n" + "".join(f"\n[[files]]\n{block}\n" for block in ordered), encoding="utf-8", newline="\n")

    pack_toml = pack / "pack.toml"
    content = pack_toml.read_text(encoding="utf-8")
    content = re.sub(r'(\[index\][^\[]*?hash = ")[0-9a-f]+(")', lambda m: m.group(1) + sha256(index_path) + m.group(2), content, count=1)
    pack_toml.write_text(content, encoding="utf-8", newline="\n")


if __name__ == "__main__":
    main()
