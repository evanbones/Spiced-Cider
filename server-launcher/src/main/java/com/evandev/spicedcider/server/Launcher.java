package com.evandev.spicedcider.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Launcher {
    private static final Path DIR = Path.of("").toAbsolutePath();
    private static final Path SETTINGS = DIR.resolve("spicedcider-server.properties");
    private static final Path STATE = DIR.resolve(".spicedcider-server-state");
    private static final Path UNSUP_STATE = DIR.resolve(".unsup-state.json");
    private static final List<String> MIGRATED = List.of("mods", "config/openloader/packs");
    private static final Pattern LIST = Pattern.compile("There are (\\d+) of a max of \\d+ players online");
    private static final Pattern DONE = Pattern.compile("Done \\([0-9.,]+s\\)! For help");
    private static final boolean WINDOWS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");

    private final Properties settings;
    private final PackSnapshot snapshot;
    private final String java;
    private volatile Process server;
    private volatile OutputStream serverIn;
    private volatile boolean started;
    private volatile boolean restartRequested;
    private volatile boolean userStopped;
    private volatile boolean shuttingDown;
    private volatile String installed;
    private final AtomicInteger players = new AtomicInteger(-1);
    private final Object listLock = new Object();

    private Launcher(Properties settings, URI source) {
        this.settings = settings;
        this.snapshot = new PackSnapshot(source);
        this.java = ProcessHandle.current().info().command()
                .orElse(Path.of(System.getProperty("java.home"), "bin", WINDOWS ? "java.exe" : "java").toString());
    }

    public static void main(String[] args) throws Exception {
        Properties settings = loadSettings();
        URI source = readSource();
        if (source == null) {
            log("unsup.ini has no source= line; cannot update.");
            System.exit(1);
        }
        Launcher launcher = new Launcher(settings, source);
        Runtime.getRuntime().addShutdownHook(new Thread(launcher::onShutdown, "launcher shutdown"));
        launcher.startConsoleForwarder();
        System.exit(launcher.run(args));
    }

    private int run(String[] args) throws Exception {
        while (true) {
            restartRequested = false;
            started = false;
            players.set(-1);
            PackSnapshot.Result target = fetchQuietly();
            boolean updated = update();
            if (updated && target != null) {
                Files.writeString(STATE, target.fingerprint() + "\n" + target.neoforge() + "\n");
            }
            installed = target != null ? target.fingerprint() : storedFingerprint();
            String neoforge = target != null ? target.neoforge() : storedNeoForge();
            if (neoforge == null) {
                log("Could not determine the NeoForge version (pack unreachable and no previous install).");
                return 1;
            }
            Path argsFile = DIR.resolve("libraries/net/neoforged/neoforge/" + neoforge + "/" + (WINDOWS ? "win_args.txt" : "unix_args.txt"));
            if (!Files.isRegularFile(argsFile) && !installNeoForge(neoforge)) {
                return 1;
            }
            if (!Files.exists(DIR.resolve("eula.txt"))) {
                log("Set eula=true in eula.txt to accept the Minecraft EULA (https://aka.ms/MinecraftEULA).");
            }
            int code = runServer(DIR.relativize(argsFile).toString().replace('\\', '/'), args);
            if (shuttingDown || userStopped || !restartRequested) {
                return code;
            }
            log("Restarting to apply the pack update.");
        }
    }

    private boolean update() throws IOException, InterruptedException {
        Path unsup = DIR.resolve("unsup.jar");
        if (!Files.isRegularFile(unsup)) {
            log("unsup.jar is missing; skipping the pack update.");
            return false;
        }
        migrateUntrackedFiles();
        Files.createDirectories(DIR.resolve(".unsup-tmp"));
        log("Updating the pack with unsup...");
        Process process = new ProcessBuilder(java, "-jar", unsup.toString()).directory(DIR.toFile()).inheritIO().start();
        int code = process.waitFor();
        if (code != 0) {
            log("unsup exited with code " + code + "; starting with the files already installed.");
            return false;
        }
        return true;
    }

    private void migrateUntrackedFiles() throws IOException {
        if (Files.exists(UNSUP_STATE)) return;
        Path backup = null;
        for (String rel : MIGRATED) {
            Path source = DIR.resolve(rel);
            if (!Files.isDirectory(source)) continue;
            boolean empty;
            try (var stream = Files.list(source)) {
                empty = stream.findAny().isEmpty();
            }
            if (empty) continue;
            if (backup == null) {
                backup = DIR.resolve("pre-unsup-backup");
                if (Files.exists(backup)) {
                    backup = DIR.resolve("pre-unsup-backup-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
                }
            }
            Path target = backup.resolve(rel);
            Files.createDirectories(target.getParent());
            Files.move(source, target);
            log("First update: moved " + rel + " to " + DIR.relativize(target).toString().replace('\\', '/')
                    + " so only the pack's copies are installed.");
        }
    }

    private boolean installNeoForge(String version) throws IOException, InterruptedException {
        Path installer = DIR.resolve("neoforge-" + version + "-installer.jar");
        URI url = URI.create("https://maven.neoforged.net/releases/net/neoforged/neoforge/" + version + "/neoforge-" + version + "-installer.jar");
        log("Installing NeoForge " + version + "...");
        HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpResponse<Path> response = http.send(HttpRequest.newBuilder(url).timeout(Duration.ofMinutes(5)).build(),
                HttpResponse.BodyHandlers.ofFile(installer));
        if (response.statusCode() != 200) {
            Files.deleteIfExists(installer);
            log("Could not download the NeoForge installer (HTTP " + response.statusCode() + ").");
            return false;
        }
        Map<Path, byte[]> scripts = new HashMap<>();
        for (String name : List.of("run.sh", "run.bat")) {
            Path script = DIR.resolve(name);
            if (Files.isRegularFile(script)) scripts.put(script, Files.readAllBytes(script));
        }
        int code = new ProcessBuilder(java, "-jar", installer.toString(), "--installServer")
                .directory(DIR.toFile()).inheritIO().start().waitFor();
        for (Map.Entry<Path, byte[]> script : scripts.entrySet()) {
            Files.write(script.getKey(), script.getValue());
        }
        Files.deleteIfExists(installer);
        Files.deleteIfExists(DIR.resolve(installer.getFileName() + ".log"));
        if (code != 0) {
            log("The NeoForge installer failed with code " + code + ".");
            return false;
        }
        return true;
    }

    private int runServer(String argsFile, String[] extraArgs) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(java);
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (!arg.startsWith("-javaagent") && !arg.startsWith("-agentlib")) command.add(arg);
        }
        if (Files.isRegularFile(DIR.resolve("user_jvm_args.txt"))) command.add("@user_jvm_args.txt");
        command.add("@" + argsFile);
        command.add("nogui");
        command.addAll(List.of(extraArgs));
        log("Starting the server.");
        Process process = new ProcessBuilder(command)
                .directory(DIR.toFile())
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        server = process;
        serverIn = process.getOutputStream();
        Thread output = new Thread(() -> pumpOutput(process.getInputStream()), "server output");
        output.setDaemon(true);
        output.start();
        Thread watcher = null;
        if (Boolean.parseBoolean(settings.getProperty("auto_restart", "true"))) {
            watcher = new Thread(this::watch, "pack watcher");
            watcher.setDaemon(true);
            watcher.start();
        }
        int code = process.waitFor();
        output.join(5000);
        if (watcher != null) watcher.interrupt();
        server = null;
        serverIn = null;
        return code;
    }

    private void pumpOutput(InputStream in) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (!started && DONE.matcher(line).find()) started = true;
                Matcher m = LIST.matcher(line);
                if (m.find()) {
                    synchronized (listLock) {
                        players.set(Integer.parseInt(m.group(1)));
                        listLock.notifyAll();
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void startConsoleForwarder() {
        Thread thread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String command = line.trim();
                    if (command.equals("stop") || command.equals("/stop")) userStopped = true;
                    send(line);
                }
            } catch (IOException ignored) {
            }
        }, "console input");
        thread.setDaemon(true);
        thread.start();
    }

    private synchronized void send(String line) {
        OutputStream out = serverIn;
        if (out == null) return;
        try {
            out.write((line + "\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void watch() {
        long poll = Math.max(30, Long.parseLong(settings.getProperty("poll_seconds", "300")));
        try {
            while (!started) {
                if (server == null) return;
                Thread.sleep(1000);
            }
            while (true) {
                Thread.sleep(poll * 1000);
                PackSnapshot.Result latest = fetchQuietly();
                if (latest == null) continue;
                if (installed == null) {
                    installed = latest.fingerprint();
                    continue;
                }
                if (!latest.fingerprint().equals(installed)) {
                    log("The pack has a server-side update; scheduling a restart.");
                    countdownAndRestart();
                    return;
                }
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void countdownAndRestart() throws InterruptedException {
        long warning = Math.max(0, Long.parseLong(settings.getProperty("restart_warning_seconds", "300")));
        TreeSet<Long> marks = new TreeSet<>((a, b) -> Long.compare(b, a));
        marks.add(warning);
        for (long mark : new long[]{300, 120, 60, 30, 10}) {
            if (mark < warning) marks.add(mark);
        }
        long remaining = warning;
        boolean empty = false;
        for (long mark : marks) {
            if (server == null || userStopped) return;
            Thread.sleep((remaining - mark) * 1000);
            remaining = mark;
            if (onlinePlayers() == 0) {
                empty = true;
                break;
            }
            if (mark > 0) announce(mark);
        }
        if (!empty) Thread.sleep(remaining * 1000);
        if (server == null || userStopped) return;
        restartRequested = true;
        send("stop");
    }

    private int onlinePlayers() throws InterruptedException {
        synchronized (listLock) {
            players.set(-1);
            send("list");
            long deadline = System.currentTimeMillis() + 5000;
            while (players.get() < 0 && System.currentTimeMillis() < deadline) {
                listLock.wait(500);
            }
            return players.get();
        }
    }

    private void announce(long seconds) {
        String when = seconds >= 60 ? (seconds / 60) + (seconds >= 120 ? " minutes" : " minute") : seconds + " seconds";
        String text = "The modpack has been updated. The server restarts in " + when
                + ". Restart your game after that to get the update.";
        send("tellraw @a {\"text\":\"" + text + "\",\"color\":\"gold\"}");
        log(text);
    }

    private void onShutdown() {
        shuttingDown = true;
        Process process = server;
        if (process == null || !process.isAlive()) return;
        send("stop");
        try {
            if (!process.waitFor(90, TimeUnit.SECONDS)) process.destroy();
            process.waitFor(30, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    private PackSnapshot.Result fetchQuietly() {
        try {
            return snapshot.fetch();
        } catch (Exception e) {
            log("Could not check the pack for updates: " + e);
            return null;
        }
    }

    private static String storedFingerprint() {
        return storedLine(0);
    }

    private static String storedNeoForge() {
        return storedLine(1);
    }

    private static String storedLine(int index) {
        try {
            List<String> lines = Files.readAllLines(STATE);
            return lines.size() > index && !lines.get(index).isBlank() ? lines.get(index).trim() : null;
        } catch (IOException e) {
            return null;
        }
    }

    private static URI readSource() throws IOException {
        Path ini = DIR.resolve("unsup.ini");
        if (!Files.isRegularFile(ini)) return null;
        for (String line : Files.readAllLines(ini)) {
            String trimmed = line.trim();
            if (trimmed.startsWith("[")) break;
            if (trimmed.startsWith("source=")) return URI.create(trimmed.substring("source=".length()).trim());
        }
        return null;
    }

    private static Properties loadSettings() throws IOException {
        Properties properties = new Properties();
        if (Files.isRegularFile(SETTINGS)) {
            try (var reader = Files.newBufferedReader(SETTINGS)) {
                properties.load(reader);
            }
            return properties;
        }
        String defaults = """
                # Restart the server automatically when the pack gets a server-side update.
                auto_restart=true
                # How often to check the pack for updates, in seconds (minimum 30).
                poll_seconds=300
                # How long players are warned before the restart, in seconds. Restarts immediately when nobody is online.
                restart_warning_seconds=300
                """;
        Path tmp = SETTINGS.resolveSibling(SETTINGS.getFileName() + ".tmp");
        Files.writeString(tmp, defaults);
        Files.move(tmp, SETTINGS, StandardCopyOption.REPLACE_EXISTING);
        try (var reader = Files.newBufferedReader(SETTINGS)) {
            properties.load(reader);
        }
        return properties;
    }

    private static void log(String message) {
        System.out.println("[Spiced Cider launcher] " + message);
    }
}
