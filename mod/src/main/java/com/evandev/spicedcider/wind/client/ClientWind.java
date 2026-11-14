package com.evandev.spicedcider.wind.client;

import com.evandev.spicedcider.networking.WindPayload;
import com.evandev.spicedcider.wind.WindSample;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public final class ClientWind {
    private static final float SMOOTHING = 0.05F;

    private static volatile boolean active;
    private static volatile boolean openSky = true;
    private static volatile float heading;
    private static volatile float directionX;
    private static volatile float directionZ = 1.0F;
    private static volatile float strength;
    private static volatile float turbulence;
    private static volatile float time;

    private static float targetHeading;
    private static float targetSteady;
    private static float steady;
    private static float gustPeak;
    private static int gustAge;
    private static int gustLength;

    public static boolean active() {
        return active;
    }

    public static float heading() {
        return heading;
    }

    public static float directionX() {
        return directionX;
    }

    public static float directionZ() {
        return directionZ;
    }

    public static float strength() {
        return strength;
    }

    public static float turbulence() {
        return turbulence;
    }

    public static float time() {
        return time;
    }

    public static float strengthAt(double y) {
        return openSky ? Math.min(WindSample.MAX_STRENGTH, strength * WindSample.altitudeFactor(y)) : strength;
    }

    public static float exposedStrength(double x, double y, double z) {
        float local = strengthAt(y);
        return local <= 0.0F ? 0.0F : local * WindExposure.at(x, y, z);
    }

    public static float exposedStrength(BlockPos pos) {
        return exposedStrength(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static void receive(WindPayload payload) {
        targetHeading = payload.heading();
        targetSteady = payload.steady();
        gustPeak = payload.gustPeak();
        gustAge = payload.gustAge();
        gustLength = payload.gustLength();
        if (!active) {
            heading = targetHeading;
            steady = targetSteady;
            publish();
            active = true;
        }
    }

    static void tick(boolean hasCeiling) {
        openSky = !hasCeiling;
        if (!active) return;
        steady += (targetSteady - steady) * SMOOTHING;
        heading += Mth.wrapDegrees((targetHeading - heading) * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD * SMOOTHING;
        if (gustLength > 0 && ++gustAge >= gustLength) gustLength = 0;
        time += 1.0F;
        publish();
    }

    static void reset() {
        active = false;
        steady = targetSteady = strength = turbulence = 0.0F;
        gustLength = 0;
        time = 0.0F;
    }

    private static void publish() {
        float gust = WindSample.gustEnvelope(gustAge, gustLength) * gustPeak;
        float current = Math.min(WindSample.MAX_STRENGTH, steady + gust);
        directionX = -Mth.sin(heading);
        directionZ = Mth.cos(heading);
        turbulence = Mth.clamp(0.1F + 0.45F * current + 0.8F * gust, 0.0F, 1.5F);
        strength = current;
    }
}
