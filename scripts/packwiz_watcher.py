import hashlib
import json
import os
import re
import requests
import shutil
import subprocess
import time
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent if Path(__file__).resolve().parent.name == "scripts" else Path(
    r"C:\Users\evan\Documents\GitHub\Spiced-Cider")
PACK_DIR = REPO_ROOT / "pack"

SYNC_TARGETS = [
    {
        "name": "mods",
        "ext": ".jar",
        "packwiz_dir": PACK_DIR / "mods",
        "prism_dir": Path(r"C:\Users\evan\AppData\Roaming\PrismLauncher\instances\Spiced Cider Dev\minecraft\mods"),
        "cache": {}
    },
    {
        "name": "resource packs",
        "ext": ".zip",
        "packwiz_dir": PACK_DIR / "resourcepacks",
        "prism_dir": Path(r"C:\Users\evan\AppData\Roaming\PrismLauncher\instances\Spiced Cider Dev\minecraft\resourcepacks"),
        "cache": {}
    }
]

def get_sha1(filepath):
    """Calculates the SHA-1 hash of a file for the Modrinth API."""
    hasher = hashlib.sha1()
    with open(filepath, 'rb') as f:
        while chunk := f.read(8192):
            hasher.update(chunk)
    return hasher.hexdigest()

CACHE_FILE = REPO_ROOT / "build" / "packwiz_watcher_cache.json"
SETTLE_SECONDS = 2


def load_cache():
    """Restores hashes and Modrinth ids from the last run so unchanged files aren't rehashed or looked up again."""
    try:
        saved = json.loads(CACHE_FILE.read_text(encoding='utf-8'))
    except (OSError, ValueError):
        return
    for target in SYNC_TARGETS:
        target["cache"] = saved.get(target["name"], {})


def save_cache():
    CACHE_FILE.parent.mkdir(parents=True, exist_ok=True)
    tmp = CACHE_FILE.with_suffix('.tmp')
    tmp.write_text(json.dumps({target["name"]: target["cache"] for target in SYNC_TARGETS}), encoding='utf-8')
    os.replace(tmp, CACHE_FILE)


def query_modrinth(sha1s):
    """Looks up many files on Modrinth in one request. Returns {sha1: (project_id, version_id)}, or None if the request failed."""
    try:
        resp = requests.post("https://api.modrinth.com/v2/version_files",
                             json={"hashes": list(sha1s), "algorithm": "sha1"}, timeout=30)
        resp.raise_for_status()
        return {sha1: (version.get("project_id"), version.get("id")) for sha1, version in resp.json().items()}
    except Exception as e:
        print(f"  [API Warning] Modrinth lookup failed for {len(sha1s)} file(s): {e}")
        return None


def update_prism_cache(prism_files, target_config):
    """Updates the cache of Prism SHA1s and Modrinth version IDs for a specific target. Returns True if it changed."""
    cache = target_config["cache"]
    changed = False

    for filename in list(cache.keys()):
        if filename not in prism_files:
            del cache[filename]
            changed = True

    now = time.time()
    for filename, file_path in prism_files.items():
        stat = file_path.stat()
        entry = cache.get(filename)
        if entry and entry['mtime'] == stat.st_mtime and entry.get('size') == stat.st_size:
            continue
        if stat.st_size == 0 or now - stat.st_mtime < SETTLE_SECONDS:
            continue
        print(f"  [Cache] Hashing: {filename}")
        cache[filename] = {
            'mtime': stat.st_mtime,
            'size': stat.st_size,
            'sha1': get_sha1(file_path),
            'project_id': None,
            'version_id': None,
            'checked': False
        }
        changed = True

    unchecked = {entry['sha1']: entry for entry in cache.values() if not entry.get('checked')}
    if unchecked:
        print(f"  [Cache] Checking {len(unchecked)} file(s) on Modrinth...")
        found = query_modrinth(unchecked.keys())
        if found is not None:
            for sha1, entry in unchecked.items():
                entry['project_id'], entry['version_id'] = found.get(sha1, (None, None))
                entry['checked'] = True
            changed = True

    return changed


def get_packwiz_state(packwiz_dir, ext):
    """Reads all packwiz files in a directory, extracting filename and version ID."""
    state = []
    if not packwiz_dir.exists():
        return state

    for f in packwiz_dir.iterdir():
        if f.suffix == ext:
            state.append({
                'path': f,
                'filename': f.name,
                'version_id': None
            })
        elif f.suffix in ['.toml', '.pw.toml']:
            try:
                content = f.read_text(encoding='utf-8')
                filename_match = re.search(r'filename\s*=\s*(["\'])(.*?)\1', content)
                version_match = re.search(r'version\s*=\s*(["\'])(.*?)\1', content)

                filename = filename_match.group(2) if filename_match else None
                version_id = version_match.group(2) if version_match else None

                if filename:
                    state.append({
                        'path': f,
                        'filename': filename,
                        'version_id': version_id
                    })
            except Exception as e:
                print(f"Error reading {f}: {e}")
    return state

def normalize_side(match):
    side = match.group(2).lower()
    return f'side = "{"both" if side == "server" else side}"'

def fix_side(target, version_id):
    """Lowercases side (unsup matches it case-sensitively) and turns "server" into "both" so server-only mods still reach clients (integrated server)."""
    for pw_file in get_packwiz_state(target["packwiz_dir"], target["ext"]):
        if pw_file['version_id'] != version_id or pw_file['path'].suffix != '.toml':
            continue
        content = pw_file['path'].read_text(encoding='utf-8')
        fixed = re.sub(r'^side\s*=\s*(["\'])(.*?)\1', normalize_side, content, flags=re.MULTILINE)
        if fixed != content:
            pw_file['path'].write_text(fixed, encoding='utf-8')
            subprocess.run(["packwiz", "refresh"], cwd=PACK_DIR, shell=True, stdout=subprocess.DEVNULL)
            print(f"    -> Fixed side in {pw_file['path'].name}")

def sync_loop():
    print("Starting sync loop. Watching for changes in Prism Launcher...")
    
    for target in SYNC_TARGETS:
        target["packwiz_dir"].mkdir(parents=True, exist_ok=True)
        if not target["prism_dir"].exists():
            print(f"WARNING: Prism {target['name']} directory not found at:\n{target['prism_dir']}")

    load_cache()
    first_run = True

    while True:
        try:
            for target in SYNC_TARGETS:
                if not target["prism_dir"].exists():
                    continue

                # Get files matching the specific extension (.jar or .zip)
                prism_files = {f.name: f for f in target["prism_dir"].iterdir() if f.is_file() and f.suffix == target["ext"]}
                
                if first_run:
                    print(f"Found {len(prism_files)} {target['name']} in Prism. Building initial cache...")
                
                previous = dict(target["cache"])
                if update_prism_cache(prism_files, target):
                    save_cache()
                cache = target["cache"]

                if first_run:
                    continue

                removed = {name: entry for name, entry in previous.items() if name not in cache}
                added = [name for name in cache if name not in previous]
                if not removed and not added:
                    continue

                prism_versions = {entry['version_id'] for entry in cache.values() if entry['version_id']}

                # DELETIONS: a file that was in Prism on the last poll is gone now
                for pw_file in get_packwiz_state(target["packwiz_dir"], target["ext"]):
                    pw_filename = pw_file['filename']
                    pw_version = pw_file['version_id']
                    matches_removed = pw_filename in removed or (
                        pw_version and any(entry['version_id'] == pw_version for entry in removed.values()))
                    still_in_prism = pw_filename in cache or (pw_version and pw_version in prism_versions)
                    if matches_removed and not still_in_prism:
                        print(f"\n[-] Detected removal in Prism: {pw_filename}")
                        os.remove(pw_file['path'])
                        subprocess.run(["packwiz", "refresh"], cwd=PACK_DIR, shell=True, stdout=subprocess.DEVNULL)
                        print(f"    -> Removed from Packwiz.")

                # ADDITIONS: a file appeared in Prism since the last poll and packwiz doesn't have it
                packwiz_state = get_packwiz_state(target["packwiz_dir"], target["ext"])
                pw_filenames = {f['filename'] for f in packwiz_state}
                pw_versions = {f['version_id'] for f in packwiz_state if f['version_id']}

                for p_filename in added:
                    p_cache = cache[p_filename]
                    if p_filename in pw_filenames:
                        continue

                    if p_cache['version_id'] and p_cache['version_id'] in pw_versions:
                        continue

                    print(f"\n[+] Detected new item in Prism: {p_filename}")

                    if p_cache['project_id'] and p_cache['version_id']:
                        dl_url = f"https://modrinth.com/mod/{p_cache['project_id']}/version/{p_cache['version_id']}"
                        print(f"    Found on Modrinth! Importing: {dl_url}")
                        subprocess.run(["packwiz", "modrinth", "add", dl_url], cwd=PACK_DIR, shell=True)
                        fix_side(target, p_cache['version_id'])
                    else:
                        print(f"    Not found on Modrinth. Adding as local override...")
                        file_path = prism_files[p_filename]
                        shutil.copy(file_path, target["packwiz_dir"] / p_filename)
                        subprocess.run(["packwiz", "refresh"], cwd=PACK_DIR, shell=True, stdout=subprocess.DEVNULL)

            if first_run:
                print("Initial caches built successfully! Now monitoring for live changes...\n")
                first_run = False

        except Exception as e:
            print(f"Error during sync: {e}")

        time.sleep(2)

if __name__ == "__main__":
    sync_loop()