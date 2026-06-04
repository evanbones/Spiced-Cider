import hashlib
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

def query_modrinth(sha1):
    """Queries Modrinth API to identify a file by its hash."""
    url = f"https://api.modrinth.com/v2/version_file/{sha1}?algorithm=sha1"
    try:
        resp = requests.get(url, timeout=5)
        if resp.status_code == 200:
            data = resp.json()
            return data.get("project_id"), data.get("id")  # project_id, version_id
    except Exception as e:
        print(f"  [API Warning] Modrinth query failed for hash {sha1}: {e}")
    return None, None

def update_prism_cache(prism_files, target_config):
    """Updates the cache of Prism SHA1s and Modrinth version IDs for a specific target."""
    cache = target_config["cache"]
    current_filenames = set(prism_files.keys())

    # Remove deleted files from cache
    for filename in list(cache.keys()):
        if filename not in current_filenames:
            del cache[filename]

    # Add or update files in cache
    for filename, file_path in prism_files.items():
        stat = file_path.stat()

        if filename not in cache or cache[filename]['mtime'] != stat.st_mtime:
            if stat.st_size == 0:
                continue
            time.sleep(0.5)
            if stat.st_size != file_path.stat().st_size:
                continue

            print(f"  [Cache] Hashing and checking Modrinth for: {filename}...")
            sha1 = get_sha1(file_path)
            project_id, version_id = query_modrinth(sha1)
            
            cache[filename] = {
                'mtime': stat.st_mtime,
                'sha1': sha1,
                'project_id': project_id,
                'version_id': version_id
            }

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

def sync_loop():
    print("Starting sync loop. Watching for changes in Prism Launcher...")
    
    for target in SYNC_TARGETS:
        target["packwiz_dir"].mkdir(parents=True, exist_ok=True)
        if not target["prism_dir"].exists():
            print(f"WARNING: Prism {target['name']} directory not found at:\n{target['prism_dir']}")

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
                
                update_prism_cache(prism_files, target)

                packwiz_state = get_packwiz_state(target["packwiz_dir"], target["ext"])
                cache = target["cache"]

                # DELETIONS: File exists in Packwiz, but was removed from Prism
                for pw_file in packwiz_state:
                    pw_filename = pw_file['filename']
                    pw_version = pw_file['version_id']

                    in_prism = pw_filename in cache

                    if not in_prism and pw_version:
                        for p_cache in cache.values():
                            if p_cache['version_id'] == pw_version:
                                in_prism = True
                                break

                    if not in_prism:
                        print(f"\n[-] Detected removal in Prism: {pw_filename}")
                        os.remove(pw_file['path'])
                        subprocess.run(["packwiz", "refresh"], cwd=PACK_DIR, shell=True, stdout=subprocess.DEVNULL)
                        print(f"    -> Removed from Packwiz.")

                # ADDITIONS: File downloaded in Prism, but missing in Packwiz
                packwiz_state = get_packwiz_state(target["packwiz_dir"], target["ext"])
                pw_filenames = {f['filename'] for f in packwiz_state}
                pw_versions = {f['version_id'] for f in packwiz_state if f['version_id']}

                for p_filename, p_cache in cache.items():
                    if p_filename in pw_filenames:
                        continue

                    if p_cache['version_id'] and p_cache['version_id'] in pw_versions:
                        continue

                    print(f"\n[+] Detected new item in Prism: {p_filename}")

                    if p_cache['project_id'] and p_cache['version_id']:
                        dl_url = f"https://modrinth.com/mod/{p_cache['project_id']}/version/{p_cache['version_id']}"
                        print(f"    Found on Modrinth! Importing: {dl_url}")
                        subprocess.run(["packwiz", "modrinth", "add", dl_url], cwd=PACK_DIR, shell=True)
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