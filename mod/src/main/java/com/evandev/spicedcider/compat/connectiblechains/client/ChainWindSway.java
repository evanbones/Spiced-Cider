package com.evandev.spicedcider.compat.connectiblechains.client;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.wind.client.ClientWind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ChainWindSway {
    public static final float HANGING = 1.0F;
    public static final float BUNTING = 0.8F;
    public static final float BANNER = 0.7F;

    private static final float LEAN = 22.0F;
    private static final float FLUTTER = 9.0F;
    private static final float MAX_TILT = 35.0F;

    public static float angle(Vector3f chainVec, Vec3 start, float x, float y, float weight) {
        if (!ClientWind.active() || !SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.chainDecorationSway, true)) return 0.0F;
        float length = Mth.sqrt(chainVec.x() * chainVec.x() + chainVec.z() * chainVec.z());
        if (length < 1.0E-3F) return 0.0F;
        float alongX = chainVec.x() / length;
        float alongZ = chainVec.z() / length;
        double worldX = start.x + alongX * x;
        double worldY = start.y + y;
        double worldZ = start.z + alongZ * x;
        float strength = ClientWind.exposedStrength(worldX, worldY, worldZ);
        if (strength < 0.01F) return 0.0F;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return 0.0F;
        float time = level.getGameTime() + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        float phase = phase(worldX, worldY, worldZ);
        float facing = -alongZ * ClientWind.directionX() + alongX * ClientWind.directionZ();
        float gust = 0.8F + 0.2F * Mth.sin(time * 0.13F + phase);
        float lean = LEAN * strength * gust * facing;
        float flutter = FLUTTER * strength * (0.35F + ClientWind.turbulence())
                * Mth.sin(time * 0.37F + phase * 2.0F) * Mth.sin(time * 0.11F + phase);
        float degrees = Mth.clamp((lean + flutter) * weight, -MAX_TILT, MAX_TILT);
        return -degrees * Mth.DEG_TO_RAD;
    }

    private static float phase(double x, double y, double z) {
        int hash = Mth.murmurHash3Mixer(Mth.floor(x * 4.0) * 73428767 ^ Mth.floor(y * 4.0) * 9122719 ^ Mth.floor(z * 4.0) * 42317861);
        return (hash & 65535) / 65535.0F * Mth.TWO_PI;
    }
}
