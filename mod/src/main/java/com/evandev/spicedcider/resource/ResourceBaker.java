package com.evandev.spicedcider.resource;

import com.evandev.spicedcider.SpicedCider;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ResourceBaker {

    public static void bakeFromManifest(Path cacheDir, Path manifestPath, Path resourcePacksDir) {
        if (!Files.exists(manifestPath)) return;

        Path cacheZip = cacheDir.resolve("spicedcider_global_jit.zip");

        try {
            if (Files.exists(cacheZip)) {
                FileTime manifestTime = Files.getLastModifiedTime(manifestPath);
                FileTime cacheTime = Files.getLastModifiedTime(cacheZip);

                if (cacheTime.compareTo(manifestTime) >= 0) {
                    return;
                }
                Files.delete(cacheZip);
            }

            Files.createDirectories(cacheDir);

            try (Reader reader = Files.newBufferedReader(manifestPath);
                 ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(cacheZip))) {

                Gson gson = new Gson();
                PackManifest manifestObj = gson.fromJson(reader, PackManifest.class);

                if (manifestObj == null || manifestObj.packs == null) return;

                Set<String> addedEntries = new HashSet<>();

                ZipEntry metaEntry = new ZipEntry("pack.mcmeta");
                zos.putNextEntry(metaEntry);
                String mcmeta = "{\"pack\":{\"pack_format\":34,\"description\":\"Spiced Cider Global JIT\"}}";
                zos.write(mcmeta.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
                addedEntries.add("pack.mcmeta");

                for (Map.Entry<String, List<String>> entry : manifestObj.packs.entrySet()) {
                    Path packFile = resourcePacksDir.resolve(entry.getKey());
                    if (!Files.exists(packFile)) continue;

                    try (ZipFile zip = new ZipFile(packFile.toFile())) {
                        for (String targetPath : entry.getValue()) {
                            targetPath = targetPath.replace("\\", "/");
                            if (targetPath.startsWith("/")) targetPath = targetPath.substring(1);

                            if (addedEntries.contains(targetPath)) continue;

                            ZipEntry zipEntry = zip.getEntry(targetPath);
                            if (zipEntry != null && !zipEntry.isDirectory()) {

                                ensureParentDirectories(targetPath, zos, addedEntries);

                                zos.putNextEntry(new ZipEntry(targetPath));
                                try (InputStream is = zip.getInputStream(zipEntry)) {
                                    is.transferTo(zos);
                                }
                                zos.closeEntry();
                                addedEntries.add(targetPath);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            SpicedCider.LOGGER.error("Failed to bake global JIT resource pack", e);
            try {
                Files.deleteIfExists(cacheZip);
            } catch (Exception ignored) {
            }
        }
    }

    private static void ensureParentDirectories(String filePath, ZipOutputStream zos, Set<String> addedEntries) throws Exception {
        String[] parts = filePath.split("/");
        StringBuilder currentPath = new StringBuilder();

        for (int i = 0; i < parts.length - 1; i++) {
            currentPath.append(parts[i]).append("/");
            String dirPath = currentPath.toString();

            if (!addedEntries.contains(dirPath)) {
                zos.putNextEntry(new ZipEntry(dirPath));
                zos.closeEntry();
                addedEntries.add(dirPath);
            }
        }
    }
}