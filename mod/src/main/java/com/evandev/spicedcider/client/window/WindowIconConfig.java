package com.evandev.spicedcider.client.window;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WindowIconConfig {
    private static final Pattern KEY = Pattern.compile("^\\s*customWindowIcon\\s*=\\s*(true|false)\\b");
    private static Boolean fromFile;

    public static boolean enabled() {
        if (SpicedCiderConfig.STARTUP_SPEC.isLoaded()) {
            return SpicedCiderConfig.STARTUP.customWindowIcon.get();
        }
        if (fromFile == null) {
            fromFile = readFile(FMLPaths.CONFIGDIR.get().resolve("spicedcider-startup.toml"));
        }
        return fromFile;
    }

    private static boolean readFile(Path file) {
        try {
            if (!Files.isRegularFile(file)) return false;
            for (String line : Files.readAllLines(file)) {
                Matcher m = KEY.matcher(line);
                if (m.find()) return Boolean.parseBoolean(m.group(1));
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
