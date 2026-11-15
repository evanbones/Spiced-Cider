package com.evandev.spicedcider.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class PackSnapshot {
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    public record Result(String fingerprint, String neoforge, String minecraft) {
    }

    private final URI source;
    private final Map<String, String> sideCache = new ConcurrentHashMap<>();
    private String lastCacheKey;
    private Result lastResult;

    public PackSnapshot(URI source) {
        this.source = source;
    }

    public synchronized Result fetch() throws IOException, InterruptedException {
        Map<String, Map<String, String>> pack = Toml.parse(get(source));
        Map<String, String> versions = pack.getOrDefault("versions", Map.of());
        Map<String, String> index = pack.getOrDefault("index", Map.of());
        String indexFile = index.get("file");
        String indexHash = index.get("hash");
        if (indexFile == null) throw new IOException("pack.toml has no [index] file");
        TreeMap<String, String> relevant = new TreeMap<>();
        versions.forEach((k, v) -> relevant.put("versions/" + k, v));
        String cacheKey = indexHash + relevant;
        if (indexHash != null && cacheKey.equals(lastCacheKey) && lastResult != null) {
            return lastResult;
        }
        URI indexUri = resolve(source, indexFile);
        List<Map<String, String>> files = Toml.parseFiles(get(indexUri));
        List<Map<String, String>> metafiles = new ArrayList<>();
        for (Map<String, String> file : files) {
            String path = file.get("file");
            String hash = file.get("hash");
            if (path == null || hash == null) continue;
            if ("true".equals(file.get("metafile"))) {
                metafiles.add(file);
            } else {
                relevant.put(path, hash);
            }
        }
        ExecutorService pool = Executors.newFixedThreadPool(12);
        try {
            Map<Map<String, String>, Future<String>> sides = new LinkedHashMap<>();
            for (Map<String, String> file : metafiles) {
                String key = file.get("file") + "#" + file.get("hash");
                String cached = sideCache.get(key);
                if (cached != null) {
                    sides.put(file, CompletableFuture.completedFuture(cached));
                    continue;
                }
                sides.put(file, pool.submit(() -> {
                    Map<String, String> meta = Toml.parse(get(resolve(indexUri, file.get("file")))).getOrDefault("", Map.of());
                    String side = meta.getOrDefault("side", "both");
                    sideCache.put(key, side);
                    return side;
                }));
            }
            for (Map.Entry<Map<String, String>, Future<String>> entry : sides.entrySet()) {
                String side;
                try {
                    side = entry.getValue().get();
                } catch (ExecutionException e) {
                    throw new IOException("Failed to read " + entry.getKey().get("file"), e.getCause());
                }
                if (!"client".equals(side)) {
                    relevant.put(entry.getKey().get("file"), entry.getKey().get("hash"));
                }
            }
        } finally {
            pool.shutdownNow();
        }
        lastCacheKey = cacheKey;
        lastResult = new Result(fingerprint(relevant), versions.get("neoforge"), versions.get("minecraft"));
        return lastResult;
    }

    private static String fingerprint(TreeMap<String, String> entries) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            entries.forEach((k, v) -> digest.update((k + "\u0000" + v + "\n").getBytes(StandardCharsets.UTF_8)));
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    static URI resolve(URI base, String path) {
        try {
            return base.resolve(new URI(null, null, path, null).getRawPath());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(path, e);
        }
    }

    static String get(URI uri) throws IOException, InterruptedException {
        if ("file".equals(uri.getScheme())) {
            return Files.readString(Path.of(uri));
        }
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(60))
                .header("Cache-Control", "no-cache")
                .header("User-Agent", "spicedcider-server-launcher")
                .build();
        IOException failure = null;
        for (int attempt = 0; attempt < 4; attempt++) {
            if (attempt > 0) Thread.sleep(1000L * attempt);
            try {
                HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) return response.body();
                failure = new IOException("HTTP " + response.statusCode() + " for " + uri);
                if (response.statusCode() == 404) break;
            } catch (IOException e) {
                failure = e;
            }
        }
        throw failure;
    }

    static final class Toml {
        static Map<String, Map<String, String>> parse(String text) {
            Map<String, Map<String, String>> tables = new HashMap<>();
            Map<String, String> current = tables.computeIfAbsent("", k -> new HashMap<>());
            for (String raw : text.split("\r?\n")) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("[")) {
                    String name = line.replace("[", "").replace("]", "").trim();
                    current = tables.computeIfAbsent(name, k -> new HashMap<>());
                    continue;
                }
                putPair(current, line);
            }
            return tables;
        }

        static List<Map<String, String>> parseFiles(String text) {
            List<Map<String, String>> files = new ArrayList<>();
            Map<String, String> current = null;
            for (String raw : text.split("\r?\n")) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.equals("[[files]]")) {
                    current = new HashMap<>();
                    files.add(current);
                    continue;
                }
                if (line.startsWith("[")) {
                    current = null;
                    continue;
                }
                if (current != null) putPair(current, line);
            }
            return files;
        }

        private static void putPair(Map<String, String> table, String line) {
            int eq = line.indexOf('=');
            if (eq < 0) return;
            String key = line.substring(0, eq).trim();
            String value = line.substring(eq + 1).trim();
            if (key.startsWith("\"") && key.endsWith("\"") && key.length() >= 2) {
                key = key.substring(1, key.length() - 1);
            }
            table.put(key, unquote(value));
        }

        private static String unquote(String value) {
            if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                return value.substring(1, value.length() - 1);
            }
            if (!value.startsWith("\"")) return value;
            StringBuilder out = new StringBuilder();
            for (int i = 1; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '"') break;
                if (c == '\\' && i + 1 < value.length()) {
                    char n = value.charAt(++i);
                    switch (n) {
                        case 'n' -> out.append('\n');
                        case 't' -> out.append('\t');
                        case 'u' -> {
                            out.append((char) Integer.parseInt(value.substring(i + 1, i + 5), 16));
                            i += 4;
                        }
                        default -> out.append(n);
                    }
                } else {
                    out.append(c);
                }
            }
            return out.toString();
        }
    }
}
