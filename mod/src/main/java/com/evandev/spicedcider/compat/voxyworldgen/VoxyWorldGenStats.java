package com.evandev.spicedcider.compat.voxyworldgen;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.loading.FMLPaths;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VoxyWorldGenStats {

    private static final String SINGLEPLAYER_FILE = "spicedcider_voxy_lod_stats.dat";
    private static final String MULTIPLAYER_DIR = "spicedcider/voxy-lod-stats";

    private static Path sessionPath;

    private VoxyWorldGenStats() {
    }

    public static long load() {
        sessionPath = resolvePath();
        if (sessionPath == null || !Files.isRegularFile(sessionPath)) return 0L;

        try (DataInputStream in = new DataInputStream(Files.newInputStream(sessionPath))) {
            return Math.max(0L, in.readLong());
        } catch (IOException | RuntimeException e) {
            SpicedCider.LOGGER.warn("Failed to read Voxy WorldGen LOD stats from {}", sessionPath, e);
            return 0L;
        }
    }

    public static void save(long chunksReceived) {
        Path path = sessionPath;
        sessionPath = null;
        if (path == null) return;

        try {
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(path))) {
                out.writeLong(chunksReceived);
            }
        } catch (IOException | RuntimeException e) {
            SpicedCider.LOGGER.warn("Failed to write Voxy WorldGen LOD stats to {}", path, e);
        }
    }

    public static boolean enabled() {
        return SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.voxyWorldGenPersistStats, true);
    }

    private static Path resolvePath() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return null;

        IntegratedServer local = minecraft.getSingleplayerServer();
        if (local != null) return local.getWorldPath(LevelResource.ROOT).resolve(SINGLEPLAYER_FILE);

        ServerData remote = minecraft.getCurrentServer();
        if (remote == null || remote.ip == null || remote.ip.isBlank()) return null;

        return FMLPaths.GAMEDIR.get().resolve(MULTIPLAYER_DIR).resolve(sanitize(remote.ip) + ".dat");
    }

    private static String sanitize(String address) {
        String cleaned = address.replaceAll("[^A-Za-z0-9._-]", "_");
        return cleaned.length() > 96 ? cleaned.substring(0, 96) : cleaned;
    }
}
