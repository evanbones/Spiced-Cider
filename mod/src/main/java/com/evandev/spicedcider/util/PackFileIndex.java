package com.evandev.spicedcider.util;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.server.packs.PackType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class PackFileIndex {
    private static final PackFileIndex UNINDEXED = new PackFileIndex(null);

    private final LongOpenHashSet entries;

    private PackFileIndex(LongOpenHashSet entries) {
        this.entries = entries;
    }

    public static boolean indexable(Path root) {
        return root.getFileSystem() != FileSystems.getDefault();
    }

    public static PackFileIndex build(Path root, PackType type) {
        Path base = root.resolve(type.getDirectory());
        LongOpenHashSet entries = new LongOpenHashSet();
        try (Stream<Path> files = Files.walk(base)) {
            files.forEach(path -> {
                if (path.equals(base)) return;
                StringBuilder key = new StringBuilder();
                for (Path segment : base.relativize(path)) {
                    if (!key.isEmpty()) key.append('/');
                    key.append(segment);
                }
                entries.add(hash(key));
            });
        } catch (NoSuchFileException | NotDirectoryException e) {
            return new PackFileIndex(entries);
        } catch (IOException | UncheckedIOException | SecurityException e) {
            return UNINDEXED;
        }
        entries.trim();
        return new PackFileIndex(entries);
    }

    public boolean definitelyMissing(String namespace, String path) {
        if (entries == null || !isPlainPath(path)) return false;
        long hash = hash(namespace);
        if (!path.isEmpty()) hash = hashAppend(hashAppend(hash, '/'), path);
        return !entries.contains(hash);
    }

    private static boolean isPlainPath(String path) {
        if (path.isEmpty()) return true;
        if (path.charAt(0) == '/' || path.charAt(path.length() - 1) == '/' || path.contains("//")) return false;
        return !path.equals(".") && !path.equals("..") && !path.startsWith("./") && !path.startsWith("../")
                && !path.contains("/./") && !path.contains("/../") && !path.endsWith("/.") && !path.endsWith("/..");
    }

    private static long hash(CharSequence value) {
        return hashAppend(0xcbf29ce484222325L, value);
    }

    private static long hashAppend(long hash, CharSequence value) {
        for (int i = 0; i < value.length(); i++) hash = hashAppend(hash, value.charAt(i));
        return hash;
    }

    private static long hashAppend(long hash, char c) {
        return (hash ^ c) * 0x100000001b3L;
    }
}
