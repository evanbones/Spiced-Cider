package com.evandev.spicedcider.compat.beams;

import net.minecraft.world.level.Level;

public class BeamRaytrace {
    private static volatile float clientPartialTick = 1.0F;

    public static void setClientPartialTick(float partialTick) {
        clientPartialTick = partialTick;
    }

    public static float partialTick(Level level) {
        return level.isClientSide ? clientPartialTick : 1.0F;
    }
}
