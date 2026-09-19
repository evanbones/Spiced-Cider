package com.evandev.spicedcider.compat.betterclouds;

import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.fml.ModList;

public final class BetterCloudsCompat {

    public static final String MOD_ID = "betterclouds";

    private static Boolean loaded;

    private BetterCloudsCompat() {
    }

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MOD_ID);
        }
        return loaded;
    }

    public static CloudField field(ClientLevel level, float partialTick) {
        if (!isLoaded()) return null;
        try {
            return BetterCloudsBridge.field(level, partialTick);
        } catch (LinkageError | RuntimeException e) {
            return null;
        }
    }

    public static float coverage(int gridX, int gridZ, CloudField field) {
        return BetterCloudsBridge.coverage(gridX, gridZ, field);
    }

    public static void invalidate() {
        if (!isLoaded()) return;
        BetterCloudsBridge.invalidate();
    }
}
