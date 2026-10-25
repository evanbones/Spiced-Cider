package com.evandev.spicedcider.util;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ModelLocationCache {
    private static volatile Map<BlockState, ModelResourceLocation> active;

    public static void begin() {
        active = null;
        Map<BlockState, ModelResourceLocation> cache = new ConcurrentHashMap<>(1 << 18);
        BuiltInRegistries.BLOCK.entrySet().parallelStream().forEach(entry -> {
            ResourceLocation id = entry.getKey().location();
            Block block = entry.getValue();
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                cache.put(state, BlockModelShaper.stateToModelLocation(id, state));
            }
        });
        active = cache;
    }

    public static void end() {
        active = null;
    }

    public static Map<BlockState, ModelResourceLocation> active() {
        return active;
    }
}
