import argparse
import json
import shutil
import subprocess
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
PRISM_EXE = Path(r"C:\ProgramData\PrismLauncher\prismlauncher.exe")
INSTANCE = "Spiced Cider Dev"
INSTANCE_DIR = Path(r"C:\Users\evan\AppData\Roaming\PrismLauncher\instances") / INSTANCE / "minecraft"
JFR_EXE = Path(r"C:\Users\evan\AppData\Roaming\PrismLauncher\java\java-runtime-epsilon\bin\jfr.exe")
BRIDGE = "http://127.0.0.1:25599"
BENCH_TEMPLATE = INSTANCE_DIR / "devbridge" / "templates" / "Bench"
BENCH_WORLD = "Bench"
DEFAULT_VIEWS = ["hot-methods", "allocation-by-class", "gc-pauses", "contention-by-site"]


def call(method, path, params=None, body=None, timeout=900):
    url = BRIDGE + path
    if params:
        url += "?" + urllib.parse.urlencode({k: v for k, v in params.items() if v is not None})
    data = body.encode("utf-8") if body is not None else (b"" if method == "POST" else None)
    request = urllib.request.Request(url, data=data, method=method)
    try:
        with urllib.request.urlopen(request, timeout=timeout) as response:
            return json.loads(response.read())
    except urllib.error.HTTPError as e:
        return json.loads(e.read())


def bridge_up():
    try:
        call("GET", "/status", timeout=5)
        return True
    except (urllib.error.URLError, ConnectionError, TimeoutError):
        return False


def show(result):
    print(json.dumps(result, indent=2))
    if isinstance(result, dict) and result.get("ok") is False:
        sys.exit(1)


def cmd_launch(args):
    if bridge_up():
        print("Bridge already up; game is running.")
    else:
        launch = [str(PRISM_EXE), "--launch", INSTANCE]
        if args.world:
            launch += ["--world", args.world]
        subprocess.Popen(launch, close_fds=True)
        deadline = time.time() + args.timeout
        while not bridge_up():
            if time.time() > deadline:
                print("Timed out waiting for the dev bridge. Check logs/latest.log.")
                sys.exit(1)
            time.sleep(2)
        print("Bridge up.")
    if args.world:
        show(call("POST", "/wait", {"for": "world", "timeout": args.timeout}))
    else:
        show(call("POST", "/wait", {"for": "menu", "timeout": args.timeout}))


def cmd_bench(args):
    ready = call("POST", "/wait", {"for": "world", "timeout": 60})
    if not ready.get("ok"):
        print("A screen is open in-game; close it before benchmarking.")
        sys.exit(1)
    if args.stable:
        call("POST", "/wait", {"for": "stable", "n": args.stable})
    call("POST", "/stats/reset")
    if args.jfr:
        show(call("POST", "/jfr/start", {"settings": args.settings}))
    call("POST", "/wait", {"for": "seconds", "n": args.seconds})
    stats = call("GET", "/stats")
    if args.jfr:
        stats["jfr"] = call("POST", "/jfr/stop", {"name": args.name})
    if stats.get("frames", {}).get("framesWithScreenOpen"):
        stats["warning"] = "A screen was open during part of the measurement window"
    show(stats)


def cmd_reset_world():
    if bridge_up() and call("GET", "/status").get("world") is not None:
        print("Leave the world (or quit) before resetting it.")
        sys.exit(1)
    target = INSTANCE_DIR / "saves" / BENCH_WORLD
    if target.exists():
        shutil.rmtree(target)
    shutil.copytree(BENCH_TEMPLATE, target)
    print(f"Restored {target} from template.")


def cmd_kill():
    port = urllib.parse.urlparse(BRIDGE).port
    script = (f"$c = Get-NetTCPConnection -LocalPort {port} -State Listen -ErrorAction SilentlyContinue; "
              "if ($c) { Stop-Process -Id $c.OwningProcess -Force; Write-Output \"Killed $($c.OwningProcess)\" } "
              "else { Write-Output 'No process listening on the bridge port' }")
    subprocess.run(["powershell", "-NoProfile", "-Command", script])


def cmd_jfr(args):
    if args.action == "start":
        show(call("POST", "/jfr/start", {"settings": args.settings}))
    elif args.action == "stop":
        show(call("POST", "/jfr/stop", {"name": args.name}))
    else:
        if not args.file:
            print("jfr view needs a .jfr file")
            sys.exit(1)
        for view in args.views or DEFAULT_VIEWS:
            print(f"===== {view} =====", flush=True)
            subprocess.run([str(JFR_EXE), "view", "--width", "220", view, args.file])


def main():
    parser = argparse.ArgumentParser(description="Drive the Spiced Cider Dev instance through the in-game dev bridge")
    sub = parser.add_subparsers(dest="command", required=True)

    sub.add_parser("deploy", help="Build the coremod, copy it into pack/mods and run packwiz refresh")

    p = sub.add_parser("launch", help="Launch the Prism instance and wait for the bridge")
    p.add_argument("--world")
    p.add_argument("--timeout", type=int, default=900)

    sub.add_parser("status")
    sub.add_parser("reset-world", help=f"Restore saves/{BENCH_WORLD} from the benchmark template")

    p = sub.add_parser("stats")
    p.add_argument("--reset", action="store_true")

    p = sub.add_parser("cmd", help="Run commands on the integrated server and capture output")
    p.add_argument("commands", nargs="+")
    p.add_argument("--client", action="store_true")

    p = sub.add_parser("shot", help="Save a screenshot (HUD hidden unless --hud)")
    p.add_argument("--name")
    p.add_argument("--hud", action="store_true")

    p = sub.add_parser("wait")
    p.add_argument("condition", choices=["world", "menu", "ticks", "frames", "seconds", "stable"])
    p.add_argument("n", nargs="?", type=int, default=1)
    p.add_argument("--timeout", type=int, default=600)

    p = sub.add_parser("join")
    p.add_argument("world")
    sub.add_parser("leave")
    sub.add_parser("quit")
    sub.add_parser("kill", help="Force-kill the game process that owns the bridge port (for a hung client)")

    p = sub.add_parser("bench", help="Reset frame stats, measure for N seconds, optionally under JFR")
    p.add_argument("--seconds", type=int, default=30)
    p.add_argument("--stable", type=int, default=0, help="First wait until loaded chunks are unchanged for N seconds")
    p.add_argument("--jfr", action="store_true")
    p.add_argument("--settings", default="profile")
    p.add_argument("--name")

    p = sub.add_parser("jfr")
    p.add_argument("action", choices=["start", "stop", "view"])
    p.add_argument("file", nargs="?")
    p.add_argument("--settings", default="profile")
    p.add_argument("--name")
    p.add_argument("--views", nargs="*")

    args = parser.parse_args()

    match args.command:
        case "deploy":
            gradlew = REPO_ROOT / ("gradlew.bat" if sys.platform == "win32" else "gradlew")
            sys.exit(subprocess.run([str(gradlew), ":mod:deployToPack"], cwd=REPO_ROOT).returncode)
        case "launch":
            cmd_launch(args)
        case "reset-world":
            cmd_reset_world()
        case "status":
            show(call("GET", "/status"))
        case "stats":
            show(call("POST", "/stats/reset") if args.reset else call("GET", "/stats"))
        case "cmd":
            show(call("POST", "/command", {"side": "client" if args.client else None}, "\n".join(args.commands)))
        case "shot":
            show(call("POST", "/screenshot", {"name": args.name, "hud": "1" if args.hud else None}))
        case "wait":
            show(call("POST", "/wait", {"for": args.condition, "n": args.n, "timeout": args.timeout}))
        case "join":
            show(call("POST", "/join", {"world": args.world}))
        case "leave":
            show(call("POST", "/leave"))
        case "quit":
            show(call("POST", "/quit"))
        case "kill":
            cmd_kill()
        case "bench":
            cmd_bench(args)
        case "jfr":
            cmd_jfr(args)


if __name__ == "__main__":
    try:
        main()
    except urllib.error.URLError:
        print(f"Dev bridge not reachable at {BRIDGE}. Is the game running with -Dspicedcider.devbridge?")
        sys.exit(2)
