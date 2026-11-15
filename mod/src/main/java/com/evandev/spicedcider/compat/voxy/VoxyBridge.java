package com.evandev.spicedcider.compat.voxy;

import com.evandev.spicedcider.SpicedCider;
import me.cortex.voxy.client.VoxyClientInstance;
import me.cortex.voxy.client.config.VoxyConfig;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import me.cortex.voxy.commonImpl.VoxyCommon;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class VoxyBridge {
    private static final String STORE_ID_FILE = "spicedcider_lod_store";
    @Nullable
    private static Path storePath;
    @Nullable
    private static UUID storeId;

    @Nullable
    public static UUID storeId() {
        if (!(VoxyCommon.getInstance() instanceof VoxyClientInstance instance)) return null;
        Path base = instance.getStorageBasePath();
        if (base == null) return null;
        if (base.equals(storePath)) return storeId;
        Path file = base.resolve(STORE_ID_FILE);
        UUID id = null;
        try {
            if (Files.isRegularFile(file)) id = UUID.fromString(Files.readString(file, StandardCharsets.UTF_8).trim());
        } catch (IOException | IllegalArgumentException exception) {
            SpicedCider.LOGGER.warn("Unreadable Voxy store id at {}, replacing it", file);
        }
        if (id == null) {
            id = UUID.randomUUID();
            try {
                Files.createDirectories(base);
                Files.writeString(file, id.toString(), StandardCharsets.UTF_8);
            } catch (IOException exception) {
                SpicedCider.LOGGER.warn("Could not save Voxy store id to {}", file, exception);
                return null;
            }
        }
        storePath = base;
        storeId = id;
        return id;
    }

    public static float lodDistance() {
        VoxyConfig config = VoxyConfig.CONFIG;
        if (config == null || !config.isRenderingEnabled() || IGetVoxyRenderSystem.getNullable() == null) return -1;
        return config.sectionRenderDistance * 512.0F;
    }

    public static int lodRadiusChunks() {
        VoxyConfig config = VoxyConfig.CONFIG;
        if (config == null || !config.isRenderingEnabled()) return 0;
        return (int) Math.ceil(config.sectionRenderDistance * 32.0F);
    }
}
