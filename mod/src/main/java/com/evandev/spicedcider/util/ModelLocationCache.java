package com.evandev.spicedcider.util;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ModelLocationCache {
    private static volatile Map<BlockState, ModelResourceLocation> active;

    private ModelLocationCache() {
    }

    public static void begin() {
        active = new ConcurrentHashMap<>(1 << 18);
    }

    public static void end() {
        active = null;
    }

    public static Map<BlockState, ModelResourceLocation> active() {
        return active;
    }
}
