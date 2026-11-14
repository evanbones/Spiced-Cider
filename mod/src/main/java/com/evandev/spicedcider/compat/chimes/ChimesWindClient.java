package com.evandev.spicedcider.compat.chimes;

import com.evandev.spicedcider.wind.client.ClientWind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public final class ChimesWindClient {
    private static final float CALM_SOUND_STRENGTH = 0.05F;
    private static final float BASELINE_STRENGTH = 0.35F;

    private static double phase;
    private static float rate = 1.0F;
    private static long lastTick = Long.MIN_VALUE;
    private static ClientLevel lastLevel;

    public static float phase(float partialTicks, BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            long tick = level.getGameTime();
            if (level != lastLevel || tick < lastTick) {
                lastLevel = level;
                lastTick = tick;
            }
            long steps = Math.min(tick - lastTick, 20L);
            for (long i = 0; i < steps; i++) phase += rate;
            lastTick = tick;
            rate = ClientWind.active() ? 0.4F + 1.6F * ClientWind.strength() : 1.0F;
        }
        return (float) (phase + rate * partialTicks) + pos.getX() * 7 + pos.getZ() * 13;
    }

    public static float strength(BlockPos pos) {
        if (!ClientWind.active()) return BASELINE_STRENGTH;
        return ClientWind.exposedStrength(pos);
    }

    public static float amplitude(BlockPos pos) {
        return Mth.clamp(0.15F + 2.4F * strength(pos), 0.15F, 2.5F);
    }

    public static float soundFactor(BlockPos pos) {
        float strength = strength(pos);
        if (strength < CALM_SOUND_STRENGTH) return 0.0F;
        return Mth.clamp(strength / BASELINE_STRENGTH, 0.0F, 3.0F);
    }
}
