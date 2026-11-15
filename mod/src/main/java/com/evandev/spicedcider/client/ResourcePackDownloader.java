package com.evandev.spicedcider.client;

import com.evandev.spicedcider.SpicedCider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.progress.ProgressMeter;
import net.neoforged.fml.loading.progress.StartupNotificationManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class ResourcePackDownloader {
    private static final String ALLOWED_HOST = "https://cdn.modrinth.com/";
    private static final Pattern SHA512 = Pattern.compile("[0-9a-f]{128}");
    private static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get().resolve("spicedcider");
    private static final Path MANIFEST = CONFIG_DIR.resolve("resource_pack_downloads.json");
    private static final Path INSTALLED = CONFIG_DIR.resolve("resource_pack_downloads_installed.json");

    private record Pack(String filename, String url, String sha512) {
    }

    public static void run(String version) {
        if (!Files.isRegularFile(MANIFEST)) return;
        List<Pack> packs;
        try {
            packs = readManifest();
        } catch (IOException | RuntimeException e) {
            SpicedCider.LOGGER.error("Could not read {}", MANIFEST, e);
            return;
        }
        Path dir = FMLPaths.GAMEDIR.get().resolve("resourcepacks");
        try {
            Files.createDirectories(dir);
            removeStale(dir, packs);
        } catch (IOException e) {
            SpicedCider.LOGGER.error("Could not prepare {}", dir, e);
            return;
        }
        List<Pack> missing = new ArrayList<>();
        for (Pack pack : packs) {
            if (!matches(dir.resolve(pack.filename()), pack.sha512())) missing.add(pack);
        }
        if (!missing.isEmpty()) download(dir, missing, version);
        writeInstalled(packs);
    }

    private static List<Pack> readManifest() throws IOException {
        JsonObject root = JsonParser.parseString(Files.readString(MANIFEST, StandardCharsets.UTF_8)).getAsJsonObject();
        List<Pack> packs = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("packs")) {
            JsonObject object = element.getAsJsonObject();
            Pack pack = new Pack(object.get("filename").getAsString(), object.get("url").getAsString(),
                    object.get("sha512").getAsString().toLowerCase());
            if (!validFilename(pack.filename()) || !pack.url().startsWith(ALLOWED_HOST) || !SHA512.matcher(pack.sha512()).matches()) {
                throw new IllegalArgumentException("Invalid resource pack entry " + object);
            }
            packs.add(pack);
        }
        return packs;
    }

    private static boolean validFilename(String name) {
        return name.endsWith(".zip") && !name.startsWith(".") && name.indexOf('/') < 0 && name.indexOf('\\') < 0
                && !name.contains("..") && name.chars().noneMatch(c -> c < 0x20);
    }

    private static void removeStale(Path dir, List<Pack> packs) throws IOException {
        if (!Files.isRegularFile(INSTALLED)) return;
        Set<String> current = new LinkedHashSet<>();
        packs.forEach(pack -> current.add(pack.filename()));
        JsonArray previous = JsonParser.parseString(Files.readString(INSTALLED, StandardCharsets.UTF_8)).getAsJsonArray();
        for (JsonElement element : previous) {
            String name = element.getAsString();
            if (validFilename(name) && !current.contains(name) && Files.deleteIfExists(dir.resolve(name))) {
                SpicedCider.LOGGER.info("Removed outdated resource pack {}", name);
            }
        }
    }

    private static void download(Path dir, List<Pack> missing, String version) {
        ProgressMeter bar = StartupNotificationManager.addProgressBar("Downloading resource packs", missing.size());
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        String userAgent = "Cider-Core/" + version;
        int downloaded = 0;
        try {
            for (Pack pack : missing) {
                bar.label("Downloading " + pack.filename());
                Path target = dir.resolve(pack.filename());
                Path part = dir.resolve(pack.filename() + ".part");
                try {
                    HttpRequest request = HttpRequest.newBuilder(URI.create(pack.url()))
                            .timeout(Duration.ofMinutes(2))
                            .header("User-Agent", userAgent)
                            .GET()
                            .build();
                    HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(part));
                    if (response.statusCode() != 200) throw new IOException("HTTP " + response.statusCode());
                    if (!matches(part, pack.sha512())) throw new IOException("sha512 mismatch");
                    move(part, target);
                    downloaded++;
                } catch (ConnectException | HttpConnectTimeoutException | UnknownHostException e) {
                    SpicedCider.LOGGER.warn("Could not reach {}; missing resource packs will be downloaded on a later launch", ALLOWED_HOST);
                    Files.deleteIfExists(part);
                    break;
                } catch (IOException e) {
                    SpicedCider.LOGGER.error("Could not download resource pack {}", pack.filename(), e);
                    Files.deleteIfExists(part);
                }
                bar.increment();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            SpicedCider.LOGGER.error("Could not clean up a partial resource pack download", e);
        } finally {
            bar.complete();
        }
        SpicedCider.LOGGER.info("Downloaded {} of {} missing resource packs", downloaded, missing.size());
    }

    private static void move(Path from, Path to) throws IOException {
        try {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static boolean matches(Path file, String sha512) {
        if (!Files.isRegularFile(file)) return false;
        try (InputStream in = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] buffer = new byte[1 << 16];
            for (int read; (read = in.read(buffer)) > 0; ) digest.update(buffer, 0, read);
            return HexFormat.of().formatHex(digest.digest()).equals(sha512);
        } catch (IOException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    private static void writeInstalled(List<Pack> packs) {
        JsonArray names = new JsonArray();
        packs.forEach(pack -> names.add(pack.filename()));
        try {
            Files.createDirectories(CONFIG_DIR);
            Files.writeString(INSTALLED, names.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            SpicedCider.LOGGER.error("Could not write {}", INSTALLED, e);
        }
    }
}
