import os
import hashlib
import json
import re
import shutil
import zipfile
import tkinter as tk
from tkinter import ttk, messagebox
from pathlib import Path
from PIL import Image, ImageTk

# ==========================================
# Paths & Constants
# ==========================================
REPO_ROOT = Path(__file__).resolve().parent.parent if Path(__file__).resolve().parent.name == "scripts" else Path(
    r"C:\Users\evan\Documents\GitHub\Spiced-Cider")

PACKWIZ_RP_DIR = REPO_ROOT / "pack" / "resourcepacks"
PRISM_RP_DIR = Path(r"C:\Users\evan\AppData\Roaming\PrismLauncher\instances\Spiced Cider Dev\minecraft\resourcepacks")

CIDER_PACKS_DIR = REPO_ROOT / "ciderpacks"
MANIFEST_FILE = REPO_ROOT / "pack" / "config" / "spicedcider" / "spicedcider_manifest.json"
CACHE_DIR = Path(r"C:\Users\evan\AppData\Roaming\PrismLauncher\instances\Spiced Cider Dev\minecraft\.spicedcider_cache")


class ConflictResolverApp:
    def __init__(self, parent):
        self.window = tk.Toplevel(parent)
        self.window.title("Spiced Cider - Conflict Resolver")
        self.window.geometry("1000x600")

        self.conflicts = {}  # Format: { "assets/minecraft/textures/...png": [Path1, Path2] }
        self.current_preview_widgets = []

        self.setup_ui()
        self.scan_for_conflicts()

    def setup_ui(self):
        left_frame = ttk.Frame(self.window, padding=10)
        left_frame.pack(side=tk.LEFT, fill=tk.Y)

        ttk.Label(left_frame, text="Detected Conflicts:", font=("Arial", 12, "bold")).pack(anchor=tk.W)

        list_frame = ttk.Frame(left_frame)
        list_frame.pack(fill=tk.BOTH, expand=True, pady=5)

        scrollbar = ttk.Scrollbar(list_frame)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)

        self.conflict_listbox = tk.Listbox(list_frame, width=50, yscrollcommand=scrollbar.set, selectmode=tk.EXTENDED)
        self.conflict_listbox.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scrollbar.config(command=self.conflict_listbox.yview)

        self.conflict_listbox.bind('<<ListboxSelect>>', self.on_select_conflict)

        ttk.Button(left_frame, text="Rescan Packs", command=self.scan_for_conflicts).pack(fill=tk.X, pady=5)

        self.right_frame = ttk.Frame(self.window, padding=10)
        self.right_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        self.preview_title = ttk.Label(self.right_frame, text="Select a conflict to preview",
                                       font=("Arial", 14, "bold"))
        self.preview_title.pack(pady=(0, 10))

        self.previews_container = ttk.Frame(self.right_frame)
        self.previews_container.pack(fill=tk.BOTH, expand=True)

    def scan_for_conflicts(self):
        self.conflict_listbox.delete(0, tk.END)
        self.conflicts.clear()

        if not CIDER_PACKS_DIR.exists():
            messagebox.showerror("Error", f"Directory not found:\n{CIDER_PACKS_DIR}", parent=self.window)
            return

        all_files = {}

        for pack_dir in CIDER_PACKS_DIR.iterdir():
            if not pack_dir.is_dir(): continue

            for root, _, files in os.walk(pack_dir):
                for file in files:
                    full_path = Path(root) / file
                    rel_path = full_path.relative_to(pack_dir).as_posix()

                    if rel_path not in all_files:
                        all_files[rel_path] = []
                    all_files[rel_path].append(full_path)

        for rel_path, paths in all_files.items():
            if len(paths) > 1:
                self.conflicts[rel_path] = paths
                self.conflict_listbox.insert(tk.END, rel_path)

        self.preview_title.config(text=f"Found {len(self.conflicts)} conflicts.")
        self.clear_previews()

    def clear_previews(self):
        for widget in self.current_preview_widgets:
            widget.destroy()
        self.current_preview_widgets.clear()

    def on_select_conflict(self, event):
        selections = self.conflict_listbox.curselection()
        if not selections: return

        self.clear_previews()

        if len(selections) == 1:
            rel_path = self.conflict_listbox.get(selections[0])
            conflicting_files = self.conflicts[rel_path]

            self.preview_title.config(text=rel_path)

            for file_path in conflicting_files:
                pack_name = file_path.relative_to(CIDER_PACKS_DIR).parts[0]

                col_frame = ttk.Frame(self.previews_container, relief=tk.GROOVE, borderwidth=2, padding=5)
                col_frame.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=5)
                self.current_preview_widgets.append(col_frame)

                ttk.Label(col_frame, text=pack_name, font=("Arial", 11, "bold")).pack(pady=5)

                if file_path.suffix.lower() == '.png':
                    self.render_image(col_frame, file_path)
                elif file_path.suffix.lower() in ['.jem', '.json', '.txt', '.mcmeta']:
                    self.render_text_snippet(col_frame, file_path)
                else:
                    ttk.Label(col_frame, text="No preview available").pack(pady=20)

                delete_btn = ttk.Button(
                    col_frame,
                    text=f"Delete from {pack_name}",
                    command=lambda p=file_path: self.delete_file(p)
                )
                delete_btn.pack(side=tk.BOTTOM, pady=10)

        else:
            self.preview_title.config(text=f"{len(selections)} conflicts selected")
            
            involved_packs = set()
            for idx in selections:
                rel_path = self.conflict_listbox.get(idx)
                for file_path in self.conflicts[rel_path]:
                    pack_name = file_path.relative_to(CIDER_PACKS_DIR).parts[0]
                    involved_packs.add(pack_name)

            col_frame = ttk.Frame(self.previews_container, relief=tk.GROOVE, borderwidth=2, padding=10)
            col_frame.pack(fill=tk.BOTH, expand=True, padx=50, pady=20)
            self.current_preview_widgets.append(col_frame)

            ttk.Label(col_frame, text="Bulk Resolution Actions", font=("Arial", 14, "bold")).pack(pady=10)
            ttk.Label(col_frame, text="Delete the selected files from the following packs:").pack(pady=5)

            for pack_name in sorted(involved_packs):
                btn = ttk.Button(
                    col_frame,
                    text=f"Delete Selected from {pack_name}",
                    command=lambda p=pack_name, s=selections: self.bulk_delete(p, s)
                )
                btn.pack(pady=5, fill=tk.X, padx=100)

    def render_image(self, parent, path):
        try:
            img = Image.open(path)
            img.thumbnail((250, 250), Image.Resampling.NEAREST)
            if img.width < 50:
                img = img.resize((img.width * 5, img.height * 5), Image.Resampling.NEAREST)

            photo = ImageTk.PhotoImage(img)
            lbl = tk.Label(parent, image=photo)
            lbl.image = photo
            lbl.pack(pady=10)

            original_size = Image.open(path).size
            ttk.Label(parent, text=f"Resolution: {original_size[0]}x{original_size[1]}").pack()
        except Exception as e:
            ttk.Label(parent, text=f"Error loading image:\n{e}").pack()

    def render_text_snippet(self, parent, path):
        try:
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read(500)

            text_widget = tk.Text(parent, width=30, height=15, wrap=tk.WORD, font=("Consolas", 9))
            text_widget.insert(tk.END, content + ("..." if len(content) == 500 else ""))
            text_widget.config(state=tk.DISABLED)
            text_widget.pack(pady=10, fill=tk.BOTH, expand=True)
        except Exception as e:
            ttk.Label(parent, text=f"Error reading file:\n{e}").pack()

    def delete_file(self, path):
        if messagebox.askyesno("Confirm Delete", f"Are you sure you want to delete:\n{path.name}\nfrom this pack?", parent=self.window):
            try:
                os.remove(path)
                messagebox.showinfo("Success", "File deleted.", parent=self.window)
                self.scan_for_conflicts()
            except Exception as e:
                messagebox.showerror("Error", f"Failed to delete file:\n{e}", parent=self.window)

    def bulk_delete(self, target_pack, selections):
        if messagebox.askyesno("Confirm Bulk Delete", f"Are you sure you want to delete ALL {len(selections)} selected files from '{target_pack}'?\n\n(Files that don't exist in this pack will be ignored).", parent=self.window):
            deleted_count = 0
            for idx in selections:
                rel_path = self.conflict_listbox.get(idx)
                for file_path in self.conflicts[rel_path]:
                    if file_path.relative_to(CIDER_PACKS_DIR).parts[0] == target_pack:
                        try:
                            os.remove(file_path)
                            deleted_count += 1
                        except Exception as e:
                            print(f"Failed to delete {file_path}: {e}")
            
            messagebox.showinfo("Bulk Delete Complete", f"Successfully deleted {deleted_count} files from {target_pack}.", parent=self.window)
            self.scan_for_conflicts()


def get_file_hash(filepath):
    hasher = hashlib.sha256()
    with open(filepath, 'rb') as f:
        while chunk := f.read(8192):
            hasher.update(chunk)
    return hasher.hexdigest()

def get_valid_pack_names():
    valid_names = set()
    if PACKWIZ_RP_DIR.exists():
        for f in PACKWIZ_RP_DIR.iterdir():
            if f.suffix == '.zip':
                valid_names.add(f.stem)
            elif f.suffix in ['.toml', '.pw.toml']:
                with open(f, 'r', encoding='utf-8') as toml_file:
                    content = toml_file.read()
                    match = re.search(r'filename\s*=\s*(["\'])(.*?)\1', content)
                    if match:
                        filename = match.group(2)
                        if filename.endswith('.zip'):
                            valid_names.add(filename[:-4])
                        else:
                            valid_names.add(filename)
    return valid_names

def extract_packs():
    try:
        print("Extracting resource packs...")
        CIDER_PACKS_DIR.mkdir(parents=True, exist_ok=True)
        PRISM_RP_DIR.mkdir(parents=True, exist_ok=True)

        valid_names = get_valid_pack_names()

        for cider_pack in CIDER_PACKS_DIR.iterdir():
            if cider_pack.is_dir() and cider_pack.name not in valid_names:
                print(f"  Removing stale extracted folder: {cider_pack.name}/")
                shutil.rmtree(cider_pack)

        for pack_name in valid_names:
            zip_path = PRISM_RP_DIR / f"{pack_name}.zip"
            target_dir = CIDER_PACKS_DIR / pack_name

            if zip_path.exists() and not target_dir.exists():
                print(f"  Unzipping {zip_path.name} -> {pack_name}/")
                with zipfile.ZipFile(zip_path, 'r') as zip_ref:
                    zip_ref.extractall(target_dir)
            elif not zip_path.exists():
                print(f"  WARNING: Missing physical zip in Prism: {zip_path.name}. Launch the game to download it.")

        print("Done extracting!")
        messagebox.showinfo("Success", "Extraction and cleanup complete!")
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred during extraction:\n{e}")

def build_manifest():
    try:
        print("Building manifest...")
        if CACHE_DIR.exists():
            print("  Clearing old Java JIT cache...")
            shutil.rmtree(CACHE_DIR)

        MANIFEST_FILE.parent.mkdir(parents=True, exist_ok=True)

        manifest_data = {"packs": {}}

        for cider_pack in CIDER_PACKS_DIR.iterdir():
            if not cider_pack.is_dir(): continue

            zip_name = cider_pack.name + ".zip"
            zip_path = PRISM_RP_DIR / zip_name

            if not zip_path.exists():
                print(f"  WARNING: Physical zip {zip_name} not found in Prism folder. Skipping hash compare.")
                continue

            print(f"  Processing {cider_pack.name}...")

            original_hashes = {}
            with zipfile.ZipFile(zip_path, 'r') as zip_ref:
                for zip_info in zip_ref.infolist():
                    if not zip_info.is_dir():
                        with zip_ref.open(zip_info) as f:
                            original_hashes[zip_info.filename] = hashlib.sha256(f.read()).hexdigest()

            manifest_data["packs"][zip_name] = []

            for root, _, files in os.walk(cider_pack):
                for file in files:
                    full_path = Path(root) / file
                    rel_path = full_path.relative_to(cider_pack).as_posix()
                    file_hash = get_file_hash(full_path)

                    if rel_path in original_hashes and original_hashes[rel_path] == file_hash:
                        manifest_data["packs"][zip_name].append(rel_path)

        with open(MANIFEST_FILE, "w", encoding='utf-8') as f:
            json.dump(manifest_data, f, indent=4)

        print(f"  Manifest written to {MANIFEST_FILE}")
        messagebox.showinfo("Success", f"Manifest successfully built!")
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred while building the manifest:\n{e}")

def restore_from_manifest():
    if not MANIFEST_FILE.exists():
        messagebox.showerror("Error", "Manifest file not found. Nothing to restore.")
        return

    if not messagebox.askyesno("Confirm Restore",
                               "This will completely overwrite your current ciderpacks folder with the state defined in the manifest.\n\nAny unsaved conflict resolutions will be lost. Proceed?"):
        return

    try:
        print("Restoring ciderpacks from manifest...")
        with open(MANIFEST_FILE, 'r', encoding='utf-8') as f:
            manifest_data = json.load(f)

        packs = manifest_data.get("packs", {})

        if CIDER_PACKS_DIR.exists():
            shutil.rmtree(CIDER_PACKS_DIR)
        CIDER_PACKS_DIR.mkdir(parents=True, exist_ok=True)

        for zip_name, files_to_keep in packs.items():
            pack_name = zip_name[:-4] if zip_name.endswith('.zip') else zip_name
            zip_path = PRISM_RP_DIR / zip_name

            if not zip_path.exists():
                print(f"  WARNING: Cannot restore {pack_name}. Missing physical zip in Prism: {zip_name}")
                continue

            target_dir = CIDER_PACKS_DIR / pack_name
            target_dir.mkdir(parents=True, exist_ok=True)

            print(f"  Restoring {pack_name} ({len(files_to_keep)} files)...")
            with zipfile.ZipFile(zip_path, 'r') as zip_ref:
                for rel_path in files_to_keep:
                    try:
                        zip_internal_path = rel_path.replace('\\', '/')
                        zip_ref.extract(zip_internal_path, path=target_dir)
                    except KeyError:
                        print(f"    WARNING: File {zip_internal_path} not found in {zip_name}")

        print("Done restoring!")
        messagebox.showinfo("Success", "Successfully restored ciderpacks from the manifest!")
    except Exception as e:
        messagebox.showerror("Error", f"An error occurred during restore:\n{e}")


def open_conflict_resolver(root):
    ConflictResolverApp(root)

def run_gui():
    root = tk.Tk()
    root.title("Cider Toolkit")

    window_width = 300
    window_height = 250
    root.geometry(f"{window_width}x{window_height}")
    root.eval('tk::PlaceWindow . center')

    label = tk.Label(root, text="Select an action to perform:", pady=10)
    label.pack()

    btn_extract = tk.Button(root, text="Extract Packs", command=extract_packs, width=20, pady=5)
    btn_extract.pack(pady=5)

    btn_restore = tk.Button(root, text="Restore from Manifest", command=restore_from_manifest, width=20, pady=5)
    btn_restore.pack(pady=5)

    btn_build = tk.Button(root, text="Build Manifest", command=build_manifest, width=20, pady=5)
    btn_build.pack(pady=5)

    btn_resolve = tk.Button(root, text="Open Conflict Resolver", command=lambda: open_conflict_resolver(root), width=20, pady=5)
    btn_resolve.pack(pady=5)

    root.mainloop()

if __name__ == "__main__":
    run_gui()