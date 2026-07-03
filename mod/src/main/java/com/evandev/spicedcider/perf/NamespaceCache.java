package com.evandev.spicedcider.perf;

import com.evandev.spicedcider.SpicedCider;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NamespaceCache {
    public static Set<String> VALID_NAMESPACES = Collections.emptySet();
    private static volatile boolean initialized = false;

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        Set<String> builder = new HashSet<>();

        builder.add("minecraft");
        builder.add("realms");
        builder.add("c");
        builder.add("fabric");
        builder.add("forge");
        builder.add("neoforge");
        builder.add("quilt");
        builder.add("fastassetsload");
        builder.add("optifine");
        builder.add("shaders");
        builder.add("sodium");
        builder.add("iris");

        computeValidNamespaces(builder);

        VALID_NAMESPACES = Collections.unmodifiableSet(builder);
        initialized = true;

        SpicedCider.LOGGER.info("Fast asset loading: Cached {} valid namespaces.", VALID_NAMESPACES.size());
    }

    public static void computeValidNamespaces(Set<String> namespaces) {
        for (IModFileInfo modFile : ModList.get().getModFiles()) {
            Path assets = modFile.getFile().findResource("assets");
            if (assets != null && Files.isDirectory(assets)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(assets)) {
                    for (Path p : stream) {
                        if (Files.isDirectory(p)) {
                            namespaces.add(p.getFileName().toString());
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}