package com.evandev.spicedcider.wind;

import net.minecraft.util.Mth;

public record WindSample(float heading, float strength, float gust) {
    public static final WindSample CALM = new WindSample(0.0F, 0.0F, 0.0F);
    public static final float MAX_STRENGTH = 2.0F;

    public float directionX() {
        return -Mth.sin(heading);
    }

    public float directionZ() {
        return Mth.cos(heading);
    }

    public float headingDegrees() {
        return Mth.wrapDegrees(heading * Mth.RAD_TO_DEG);
    }

    public WindSample scaled(float factor) {
        return new WindSample(heading, Math.min(MAX_STRENGTH, strength * factor), gust * factor);
    }

    public static float altitudeFactor(double y) {
        return 1.0F + 0.35F * (float) Mth.clamp((y - 64.0) / 128.0, 0.0, 1.0);
    }

    public static float gustEnvelope(int age, int length) {
        if (length <= 0 || age >= length) return 0.0F;
        float wave = Mth.sin(Mth.PI * age / length);
        return wave * wave;
    }
}
