package com.evandev.spicedcider.compat.polytone;

import net.mehvahdjukaar.polytone.Polytone;
import net.mehvahdjukaar.polytone.api.ResolvedPointLight;
import net.mehvahdjukaar.polytone.content.shaders.light.ColoredLightsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class PolytoneLodLight {

    public static int lightColor(BlockState state) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return -1;
        ColoredLightsManager lights = Polytone.COLORED_LIGHTS;
        BlockState lightState = lights.lightStateOf(state);
        List<ColoredLightsManager.BlockRule> rules = lights.getBlockLights(lightState.getBlock());
        if (rules == null) return -1;
        RandomSource random = RandomSource.create(0L);
        for (ColoredLightsManager.BlockRule rule : rules) {
            if (!rule.matches(lightState, random)) continue;
            ResolvedPointLight light = rule.resolve(lightState, state, Vec3.ZERO, level);
            return light == null ? -1 : light.color();
        }
        return -1;
    }
}
