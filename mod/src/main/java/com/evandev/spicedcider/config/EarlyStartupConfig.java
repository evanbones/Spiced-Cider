package com.evandev.spicedcider.config;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EarlyStartupConfig {
    private static final Pattern LINE = Pattern.compile("^\\s*([A-Za-z0-9_]+)\\s*=\\s*(true|false)\\b");
    private static Map<String, Boolean> values;

    public static synchronized boolean get(String key) {
        if (values == null) {
            values = read(FMLPaths.CONFIGDIR.get().resolve("spicedcider-startup.toml"));
        }
        return values.getOrDefault(key, false);
    }

    private static Map<String, Boolean> read(Path file) {
        Map<String, Boolean> result = new HashMap<>();
        try {
            if (!Files.isRegularFile(file)) return result;
            for (String line : Files.readAllLines(file)) {
                Matcher m = LINE.matcher(line);
                if (m.find()) result.put(m.group(1), Boolean.parseBoolean(m.group(2)));
            }
        } catch (Exception ignored) {
        }
        return result;
    }
}
