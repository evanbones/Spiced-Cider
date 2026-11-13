package com.evandev.spicedcider.roxy.loader;

import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

public final class RoxyBundle {
    private static final Logger LOGGER = LoggerFactory.getLogger("Roxy");
    private static final String UPSTREAM_LOCATOR = "net/rasanovum/roxy/loader/RoxyLocator.class";
    private static volatile Boolean upstream;
    private static volatile boolean active;

    public static boolean active() {
        return active;
    }

    static void activate() {
        active = true;
    }

    static boolean upstreamPresent() {
        Boolean present = upstream;
        if (present == null) {
            synchronized (RoxyBundle.class) {
                present = upstream;
                if (present == null) {
                    present = findUpstream();
                    upstream = present;
                    if (present) LOGGER.info("Upstream Roxy is installed; the copy bundled with Cider Core is disabled");
                }
            }
        }
        return present;
    }

    private static boolean findUpstream() {
        ClassLoader own = RoxyBundle.class.getClassLoader();
        ClassLoader context = Thread.currentThread().getContextClassLoader();
        for (ClassLoader loader : new ClassLoader[]{own, context}) {
            if (loader != null && loader.getResource(UPSTREAM_LOCATOR) != null) return true;
        }
        List<Path> ownPaths = RoxyLocator.ownModPaths();
        Path mods;
        try {
            mods = FMLPaths.MODSDIR.get();
        } catch (RuntimeException exception) {
            return false;
        }
        if (mods == null || !Files.isDirectory(mods)) return false;
        try (Stream<Path> files = Files.list(mods)) {
            return files.filter(path -> {
                        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                        return name.endsWith(".jar") && name.contains("roxy");
                    })
                    .filter(path -> !ownPaths.contains(path.toAbsolutePath().normalize()))
                    .anyMatch(RoxyBundle::containsUpstream);
        } catch (IOException exception) {
            return false;
        }
    }

    private static boolean containsUpstream(Path jar) {
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            return zip.getEntry(UPSTREAM_LOCATOR) != null;
        } catch (IOException exception) {
            return false;
        }
    }
}
